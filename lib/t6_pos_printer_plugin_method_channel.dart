import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 't6_pos_printer_plugin_platform_interface.dart';

/// An implementation of [T6PosPrinterPluginPlatform] that uses method channels.
class MethodChannelT6PosPrinterPlugin extends T6PosPrinterPluginPlatform {
  @override
  Future<void> printText(String text) async {
    try {
      await MethodChannel('t6_pos_printer_plugin').invokeMethod('printReceipt', {'text': text});
    } catch (e) {
      throw Exception('Failed to print: $e');
    }
  }
}