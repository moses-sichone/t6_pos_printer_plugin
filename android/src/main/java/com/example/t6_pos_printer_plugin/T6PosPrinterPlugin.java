package com.example.t6_pos_printer_plugin;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.RemoteException;
import android.graphics.Typeface;
import android.util.Log;

import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.cloudpos.aidl.printer.Align;
import com.topwise.cloudpos.aidl.printer.ImageUnit;
import com.topwise.cloudpos.aidl.printer.PrintTemplate;
import com.topwise.cloudpos.aidl.printer.TextUnit;
import com.topwise.cloudpos.aidl.printer.TextUnit.TextSize;
import com.topwise.cloudpos.data.PrinterConstant.FontSize;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;


import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.embedding.engine.plugins.FlutterPlugin;

public class T6PosPrinterPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {

  private Context applicationContext;
  private AidlPrinter printerDev;
  private PrintTemplate template;
  private boolean printRunning = false;

  private final AidlPrinterListener mListen = new AidlPrinterListener.Stub() {
    @Override
    public void onError(int errorCode) throws RemoteException {
      Log.e("Printer", "Error occurred: " + errorCode);
    }

    @Override
    public void onPrintFinish() throws RemoteException {
      Log.d("Printer", "Print finished");
    }
  };

  // Implementing onAttachedToEngine
  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    applicationContext = binding.getApplicationContext();
    MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "t6_pos_printer_plugin");
    channel.setMethodCallHandler(this);

    // Bind the device service to ensure printer manager is initialized
    DeviceServiceManager.getInstance().bindDeviceService(applicationContext);
    Log.d("Printer", "Plugin attached to engine.");
  }

  // Implementing onDetachedFromEngine
  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    // Perform any necessary cleanup when the plugin is detached
    Log.d("Printer", "Plugin detached from engine.");
  }

  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result result) {
    if (call.method.equals("printerInit")) {
      // Call the initialization method
      initializeTemplate();
      result.success("Printer Initialized");
    } else if (call.method.equals("addBitmap")) {
      byte[] imageData = call.argument("imageData");
      if (imageData != null) {
        addBitmap(imageData);
        result.success("Bitmap printed successfully");
      } else {
        result.error("INVALID_ARGUMENT", "imageData is null", null);
      }
    } else if  (call.method.equals("addTextToTemplate")) {
      String text = call.argument("text");
      int fontSize = call.argument("fontSize");
      String alignment = call.argument("alignment");
      boolean isBold = call.argument("bold");
      boolean isUnderline = call.argument("underline");
      boolean isWordWrap = call.argument("wordWrap");
      int lineSpacing = call.argument("lineSpacing");
      int letterSpacing = call.argument("letterSpacing");

      System.out.println("Text: " + text);

      // Add the text to the template with formatting options
      addTextToTemplate(text, fontSize, alignment, isBold, isUnderline, isWordWrap, lineSpacing, letterSpacing);
      result.success(null); // Success callback
    } else if (call.method.equals("addTwoTextsToTemplate")) {
      // Get the parameters for the first text
      String text1 = call.argument("text1");
      int fontSize1 = call.argument("fontSize1");
      String alignment1 = call.argument("alignment1");
      boolean isBold1 = call.argument("isBold1");
      boolean isUnderline1 = call.argument("isUnderline1");
      boolean isWordWrap1 = call.argument("isWordWrap1");
      int lineSpacing1 = call.argument("lineSpacing1");
      int letterSpacing1 = call.argument("letterSpacing1");

      // Get the parameters for the second text
      String text2 = call.argument("text2");
      int fontSize2 = call.argument("fontSize2");
      String alignment2 = call.argument("alignment2");
      boolean isBold2 = call.argument("isBold2");
      boolean isUnderline2 = call.argument("isUnderline2");
      boolean isWordWrap2 = call.argument("isWordWrap2");
      int lineSpacing2 = call.argument("lineSpacing2");
      int letterSpacing2 = call.argument("letterSpacing2");

      // Add the two texts to the template
      addTwoTextsToTemplate(
              text1, fontSize1, alignment1, isBold1, isUnderline1, isWordWrap1, lineSpacing1, letterSpacing1,
              text2, fontSize2, alignment2, isBold2, isUnderline2, isWordWrap2, lineSpacing2, letterSpacing2
      );
      result.success(null); // Indicate success
    }  else if (call.method.equals("printReceipt")) {
      // Print the template after all text is added
      try {
        printReceipt();
        result.success("Printed successfully");
      } catch (RemoteException e) {
        result.error("PRINT_ERROR", "Failed to print: " + e.getMessage(), null);
      }
    } else {
      result.notImplemented();
    }
  }

  // Initialize the print template (reset and prepare it)
  private void initializeTemplate() {
    // Ensure template is properly initialized
    if (template == null) {
      Typeface typeface = Typeface.createFromAsset(applicationContext.getAssets(), "topwise.ttf");
      PrintTemplate.getInstance().init(applicationContext, typeface);
      printerDev = DeviceServiceManager.getInstance().getPrintManager();
      template = PrintTemplate.getInstance(); // Get the instance
    }
    // Reset the template to prepare it for new data
    template.clear();
    Log.d("Printer", "PrintTemplate initialized.");
  }


  // Add text to the template with formatting options
  private void addTextToTemplate(String text, int fontSize, String alignment, boolean isBold, boolean isUnderline, boolean isWordWrap, int lineSpacing, int letterSpacing) {
    Align align = Align.LEFT; // Default alignment
    if ("CENTER".equalsIgnoreCase(alignment)) {
      align = Align.CENTER;
    } else if ("RIGHT".equalsIgnoreCase(alignment)) {
      align = Align.RIGHT;
    }

    try {
      // Create and configure TextUnit
      TextUnit textUnit = new TextUnit(text, fontSize, align)
              .setBold(isBold)
              .setUnderline(isUnderline)
              .setLetterSpacing(letterSpacing);

      // Apply wordWrap and lineSpacing only for LEFT alignment
      if (align == Align.LEFT) {
        textUnit.setWordWrap(isWordWrap).setLineSpacing(Math.max(0, lineSpacing));
      }

      // Add TextUnit to the template
      template.add(textUnit);
      Log.d("Printer", "Added text: " + text + " with alignment: " + alignment);
    } catch (Exception e) {
      Log.e("Printer", "Error adding text to template: " + e.getMessage(), e);
    }
  }


  private void addTwoTextsToTemplate(
          String text1, int fontSize1, String alignment1, boolean isBold1, boolean isUnderline1, boolean isWordWrap1, int lineSpacing1, int letterSpacing1,
          String text2, int fontSize2, String alignment2, boolean isBold2, boolean isUnderline2, boolean isWordWrap2, int lineSpacing2, int letterSpacing2) {

    try {
      // Map alignment strings to Align enum
      Align align1 = Align.LEFT;
      Align align2 = Align.LEFT;

      if ("CENTER".equalsIgnoreCase(alignment1)) {
        align1 = Align.CENTER;
      } else if ("RIGHT".equalsIgnoreCase(alignment1)) {
        align1 = Align.RIGHT;
      }

      if ("CENTER".equalsIgnoreCase(alignment2)) {
        align2 = Align.CENTER;
      } else if ("RIGHT".equalsIgnoreCase(alignment2)) {
        align2 = Align.RIGHT;
      }

      // Create and configure the first text unit
      TextUnit textUnit1 = new TextUnit(text1, fontSize1, align1)
              .setBold(isBold1)
              .setUnderline(isUnderline1)
              .setLetterSpacing(letterSpacing1);

      // Apply wordWrap and lineSpacing only for LEFT alignment
      if (align1 == Align.LEFT) {
        textUnit1.setWordWrap(isWordWrap1).setLineSpacing(Math.max(0, lineSpacing1));
      }

      // Create and configure the second text unit
      TextUnit textUnit2 = new TextUnit(text2, fontSize2, align2)
              .setBold(isBold2)
              .setUnderline(isUnderline2)
              .setLetterSpacing(letterSpacing2);

      // Apply wordWrap and lineSpacing only for LEFT alignment
      if (align2 == Align.LEFT) {
        textUnit2.setWordWrap(isWordWrap2).setLineSpacing(Math.max(0, lineSpacing2));
      }

      // Add both text units to the template
      template.add(1, textUnit1, 2, textUnit2);
      Log.d("Printer", "Added two texts: " + text1 + " and " + text2);
    } catch (Exception e) {
      Log.e("Printer", "Error adding two texts: " + e.getMessage(), e);
    }
  }

  private void addBitmap(byte[] imageData) {
      if (printRunning) {
          Log.w("Printer", "Print is already running.");
          return;
      }
      Bitmap scaledBitmap = null;
      try {
          // Decode the byte array into a Bitmap
          Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

          if (originalBitmap == null) {
              Log.e("Printer", "Failed to decode bitmap from imageData.");
              return;
          }

          // Add the inverted image to the printer
          printerDev.addRuiImage(originalBitmap, 0);
          //scaledBitmap = scaleBitmapForPrinter(originalBitmap, originalBitmap.getWidth());
          //template.add(new ImageUnit(scaledBitmap, (int) (scaledBitmap.getWidth() / 1.6), scaledBitmap.getHeight()));
          Log.d("Printer", "Bitmap added to printer queue.");
      } catch (Exception e) {
          Log.e("Printer", "Error while printing bitmap: " + e.getMessage(), e);
      } finally {
          // Clear resources after printing
          clearTemplate();
          resetPrinterState();
          if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
              scaledBitmap.recycle();
          }
      }
  }

  private void clearTemplate() {
    try {
      PrintTemplate template = PrintTemplate.getInstance();
      template.clear();
      Log.d("Printer", "Template cleared.");
    } catch (Exception e) {
      Log.e("Printer", "Error clearing template: " + e.getMessage(), e);
    }
  }


  private void resetPrinterState() {
    try {
      printerDev.resetQueue(); // Cancel any pending operations
      printRunning = false; // Reset the print state
      Log.d("Printer", "Printer state reset.");
    } catch (Exception e) {
      Log.e("Printer", "Error resetting printer state: " + e.getMessage(), e);
    }
  }




  private Bitmap scaleBitmapForPrinter(Bitmap originalBitmap, int targetWidth) {
    int originalWidth = originalBitmap.getWidth();
    int originalHeight = originalBitmap.getHeight();

    // Calculate scale factor to maintain aspect ratio
    float scaleFactor = (float) targetWidth / originalWidth;

    // Calculate the new height proportionally
    int targetHeight = (int) (originalHeight * scaleFactor);

    int maxHeight = 2000; // Adjust based on your printer's capability
    if (targetHeight > maxHeight) {
      scaleFactor = (float) maxHeight / originalHeight;
      targetWidth = (int) (originalWidth * scaleFactor);
      targetHeight = maxHeight;
    }

    // Scale the bitmap
    return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
  }




  // Function to print the template
  private void printReceipt() throws RemoteException {
    // Add the content to the printer template
    printerDev.addRuiImage(template.getPrintBitmap(), 1);
    printRunning = true;
    printerDev.printRuiQueue(mListen);
    template.clear();
    resetPrinterState();
  }

  private String getCurTime() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
  }

  private Bitmap invertBitmap(Bitmap originalBitmap) {
    // Create a mutable bitmap with the same size as the original
    Bitmap invertedBitmap = Bitmap.createBitmap(
            originalBitmap.getWidth(),
            originalBitmap.getHeight(),
            originalBitmap.getConfig()
    );

    // Iterate over all pixels
    for (int y = 0; y < originalBitmap.getHeight(); y++) {
      for (int x = 0; x < originalBitmap.getWidth(); x++) {
        int pixel = originalBitmap.getPixel(x, y);

        // Get the alpha, red, green, and blue components
        int alpha = (pixel >> 24) & 0xFF;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;

        // If the pixel is transparent, make it white
        if (alpha == 0) {
          invertedBitmap.setPixel(x, y, Color.WHITE);
        } else {
          // Otherwise, make it black
          invertedBitmap.setPixel(x, y, Color.BLACK);
        }
      }
    }

    return invertedBitmap;
  }

}