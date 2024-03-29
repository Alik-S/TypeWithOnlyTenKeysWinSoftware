
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;

import static com.sun.jna.Platform.isWindows;


public class NumpadKeysInputBlock {
    private static HHOOK hhk;
    private static LowLevelKeyboardProc keyboardHook;
    private static User32 lib;

    public static void blockWindowsKey() {
        if (isWindows()) {
            new Thread(new Runnable() {

                public void run() {
                    lib = User32.INSTANCE;
                    HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
                    keyboardHook = new LowLevelKeyboardProc() {
                        public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
                            if (info.vkCode >= 96 && info.vkCode <= 105) {
                                        return new LRESULT(1);
                            }
                            return null;
                        }
                    };
                    hhk = lib.SetWindowsHookEx(13, keyboardHook, hMod, 0);

                    // This bit never returns from GetMessage
                    int result;
                    MSG msg = new MSG();
                    while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                        if (result == -1) {
                            break;
                        } else {
                            lib.TranslateMessage(msg);
                            lib.DispatchMessage(msg);
                        }
                    }
                    lib.UnhookWindowsHookEx(hhk);
                }
            }).start();
        }
    }
}