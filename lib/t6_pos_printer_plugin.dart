import 'package:flutter/services.dart';

class T6PosPrinterPlugin {
  static const MethodChannel _channel = MethodChannel('t6_pos_printer_plugin');

  // Initiate printing after all text has been added
  static Future<void> printerInit() async {
    try {
      await _channel.invokeMethod('printerInit');
    } catch (e) {
      print("Failed to print: $e");
    }
  }

  static Future<void> addBitmap(Uint8List imageData) async {
    try {
      await _channel.invokeMethod('addBitmap', {'imageData': imageData});
    } catch (e) {
      throw Exception('Failed to print bitmap: $e');
    }
  }


  // Send text with formatting options to Android
  static Future<void> addText(String text, int fontSize, String alignment, {bool bold = false, bool underline = false, bool wordWrap = false, int lineSpacing = 0, int letterSpacing = 0}) async {
    try {
      await _channel.invokeMethod('addTextToTemplate', {
        'text': text,
        'fontSize': fontSize,
        'alignment': alignment,
        'bold': bold,
        'underline': underline,
        'wordWrap': wordWrap,
        'lineSpacing': lineSpacing,
        'letterSpacing': letterSpacing,
      });
    } catch (e) {
      print("Failed to add text: $e");
    }
  }

  static Future<void> addTwoTexts(
      String text1,
      int fontSize1,
      String alignment1,
      bool isBold1,
      bool isUnderline1,
      bool isWordWrap1,
      int lineSpacing1,
      int letterSpacing1,
      String text2,
      int fontSize2,
      String alignment2,
      bool isBold2,
      bool isUnderline2,
      bool isWordWrap2,
      int lineSpacing2,
      int letterSpacing2,
      ) async {
    try {
      await _channel.invokeMethod('addTwoTextsToTemplate', {
        'text1': text1,
        'fontSize1': fontSize1,
        'alignment1': alignment1,
        'isBold1': isBold1,
        'isUnderline1': isUnderline1,
        'isWordWrap1': isWordWrap1,
        'lineSpacing1': lineSpacing1,
        'letterSpacing1': letterSpacing1,
        'text2': text2,
        'fontSize2': fontSize2,
        'alignment2': alignment2,
        'isBold2': isBold2,
        'isUnderline2': isUnderline2,
        'isWordWrap2': isWordWrap2,
        'lineSpacing2': lineSpacing2,
        'letterSpacing2': letterSpacing2,
      });
    } catch (e) {
      throw Exception('Failed to add two texts: $e');
    }
  }

  // Initiate printing after all text has been added
  static Future<void> printReceipt() async {
    try {
      await _channel.invokeMethod('printReceipt');
    } catch (e) {
      print("Failed to print: $e");
    }
  }
}
