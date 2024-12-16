import 'package:flutter_test/flutter_test.dart';
import 'package:t6_pos_printer_plugin/t6_pos_printer_plugin.dart';
import 'package:t6_pos_printer_plugin/t6_pos_printer_plugin_platform_interface.dart';
import 'package:t6_pos_printer_plugin/t6_pos_printer_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockT6PosPrinterPluginPlatform
    with MockPlatformInterfaceMixin
    implements T6PosPrinterPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<void> printText(String text) {
    // TODO: implement printText
    throw UnimplementedError();
  }
}

void main() {
  final T6PosPrinterPluginPlatform initialPlatform = T6PosPrinterPluginPlatform.instance;

  test('$MethodChannelT6PosPrinterPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelT6PosPrinterPlugin>());
  });
}
