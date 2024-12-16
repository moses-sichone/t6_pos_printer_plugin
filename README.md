# t6_pos_printer_plugin

A Flutter plugin for Topwise T6 POS printers to handle receipt printing with text and images.

# Features

Printer Initialization
Set up the printer for use.

Add Text (Single Column)
Add customizable single-column text to the print template.

Add Two Texts (Double Column)
Add two separate customizable text items in the same line (e.g., name & price).

Add Bitmap Image
Add a bitmap image, like a logo or QR code, to the print template.

Initiate Printing
Send the prepared template to the printer.

# Installation

dependencies:
t6_pos_printer_plugin: ^0.0.1

# Usage

import 'package:t6_pos_printer_plugin/t6_pos_printer_plugin.dart';

void main() async {
await T6PosPrinterPlugin.initializePrinter();

// Add single text
await T6PosPrinterPlugin.addText(
"Welcome to Topwise!",
20,
"center",
bold: true,
);

// Add double-column text
await T6PosPrinterPlugin.addTwoTexts(
"Item 1", 18, "left", false, false, true, 5, 0,
"\$9.99", 18, "right", true, false, true, 5, 0,
);

// Add an image (ensure image is converted to Uint8List)
Uint8List imageData = await getImageData('assets/logo.png');
await T6PosPrinterPlugin.addBitmap(imageData);

// Print the receipt
await T6PosPrinterPlugin.printReceipt();
}

// Helper function to load image as Uint8List
Future<Uint8List> getImageData(String assetPath) async {
  final ByteData data = await rootBundle.load(assetPath);
  return data.buffer.asUint8List();
}

# API Reference

1. Printer Initialization

   Future<void> initializePrinter() async
   Initializes the printer. Must be called before adding content to the template.
   Example:
   await T6PosPrinterPlugin.initializePrinter();
2. Add Text (Single Column)

   static Future<void> addText(
   String text,
   int fontSize,
   String alignment, // "left", "center", or "right"
   {bool bold = false, bool underline = false, bool wordWrap = false,
   int lineSpacing = 0, int letterSpacing = 0}
   )
   Adds a single line of text to the print template.
   Example:

   await T6PosPrinterPlugin.addText(
   "Hello World!", 24, "center", bold: true,
   );
3. Add Two Texts (Double Column)

   static Future<void> addTwoTexts(
   String text1, int fontSize1, String alignment1, bool isBold1,
   bool isUnderline1, bool isWordWrap1, int lineSpacing1, int letterSpacing1,
   String text2, int fontSize2, String alignment2, bool isBold2,
   bool isUnderline2, bool isWordWrap2, int lineSpacing2, int letterSpacing2
   )
   Adds two texts on the same line (e.g., a key-value pair).
   Example:

   await T6PosPrinterPlugin.addTwoTexts(
   "Item", 18, "left", false, false, true, 5, 0,
   "\$20.00", 18, "right", true, false, true, 5, 0,
   );
4. Add Bitmap

   static Future<void> addBitmap(Uint8List imageData)
   Adds a bitmap image to the print template.
   Example:

   Uint8List imageData = await getImageData('assets/logo.png');
   await T6PosPrinterPlugin.addBitmap(imageData);
5. Print Receipt

   static Future<void> printReceipt()
   Finalizes the template and sends it to the printer.
   Example:
   await T6PosPrinterPlugin.printReceipt();
