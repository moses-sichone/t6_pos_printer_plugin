import 'package:flutter/material.dart';
import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
import 'package:t6_pos_printer_plugin/t6_pos_printer_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  // Function to initialize the printer
  Future<void> initializePrinter() async {
    try {
      await T6PosPrinterPlugin.printerInit();
      print("Printer Initialized Successfully");
    } catch (e) {
      print("Failed to initialize printer: $e");
    }
  }

  Future<void> _printImage() async {
    try {
      // Load the image as a byte array (replace with your image asset path)
      ByteData imageData = await rootBundle.load('assets/images/lcc_logo1.png');
      Uint8List bytes = imageData.buffer.asUint8List();

      // Call the printBitmap method
      await T6PosPrinterPlugin.addBitmap(bytes);
      print("Bitmap added successfully.");
    } catch (e) {
      print("Failed to add bitmap: $e");
    }
  }

  Future<void> addReceiptText() async {
    await initializePrinter();
    // await _printImage();
    // await T6PosPrinterPlugin.addText("\n\n", 24, "LEFT");
    await T6PosPrinterPlugin.addText("--------------------------------", 24, "CENTER");
    await T6PosPrinterPlugin.addText("Receipt", 30, "CENTER", bold: true, underline: false, letterSpacing: 2, lineSpacing: 2);
    await T6PosPrinterPlugin.addText("--------------------------------", 24, "CENTER");
    await T6PosPrinterPlugin.addText("\n\n", 24, "LEFT");
    await T6PosPrinterPlugin.addTwoTexts(
        "DATE:", 22, "LEFT", false, false, false, 5, 2,   // First text
        "${DateTime.now().day}/${DateTime.now().month}/${DateTime.now().year}", 22, "RIGHT", false, false, false, 5, 2           // Second text
    );
    await T6PosPrinterPlugin.addTwoTexts(
        "TIME:", 22, "LEFT", false, false, false, 5, 2,   // First text
        "${DateTime.now().hour}:${DateTime.now().minute}:${DateTime.now().second}", 22, "RIGHT", false, false, false, 5, 2           // Second text
    );
    await T6PosPrinterPlugin.addTwoTexts(
        "AMOUNT:", 22, "LEFT", false, false, false, 5, 2,   // First text
        "K200.00", 22, "RIGHT", false, false, false, 5, 2           // Second text
    );
    await T6PosPrinterPlugin.addTwoTexts(
        "CUSTOMER:", 22, "LEFT", false, false, false, 5, 2,   // First text
        "John Banda", 22, "RIGHT", false, false, false, 5, 2           // Second text
    );
    await T6PosPrinterPlugin.addTwoTexts(
        "CITY:", 22, "LEFT", false, false, false, 5, 2,   // First text
        "LUSAKA", 22, "RIGHT", false, false, false, 5, 2           // Second text
    );
    await T6PosPrinterPlugin.addTwoTexts(
        "DISTRICT:", 22, "LEFT", false, false, false, 5, 2,  // First text
        "LUSAKA", 22, "RIGHT", false, false, false, 5, 2           // Second text
    );
    await T6PosPrinterPlugin.addText("\n\n", 24, "LEFT");
    await T6PosPrinterPlugin.addText("--------------------------------", 24, "CENTER");
    await T6PosPrinterPlugin.addText("THANK YOU.", 24, "CENTER");
    await T6PosPrinterPlugin.addText("--------------------------------", 24, "CENTER");
    await T6PosPrinterPlugin.addText("", 24, "LEFT");
    await T6PosPrinterPlugin.addText("", 24, "LEFT");
    await T6PosPrinterPlugin.addText("", 24, "LEFT");
    await T6PosPrinterPlugin.addText("\n\n", 24, "LEFT");
    // Print the receipt after all text has been added
    await T6PosPrinterPlugin.printReceipt();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('POS Printer Plugin')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: addReceiptText,
                child: const Text("Print Receipt"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
