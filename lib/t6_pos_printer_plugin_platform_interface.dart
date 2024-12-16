import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 't6_pos_printer_plugin_method_channel.dart';

abstract class T6PosPrinterPluginPlatform extends PlatformInterface {
  // Constructor
  T6PosPrinterPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static final T6PosPrinterPluginPlatform _instance = MethodChannelT6PosPrinterPlugin();

  // Get the instance of the platform-specific implementation
  static T6PosPrinterPluginPlatform get instance => _instance;

  // Abstract method for printing text
  Future<void> printText(String text);
}
