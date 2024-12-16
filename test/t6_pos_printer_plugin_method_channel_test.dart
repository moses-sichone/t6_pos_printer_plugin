import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:t6_pos_printer_plugin/t6_pos_printer_plugin_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelT6PosPrinterPlugin platform = MethodChannelT6PosPrinterPlugin();
  const MethodChannel channel = MethodChannel('t6_pos_printer_plugin');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

}
