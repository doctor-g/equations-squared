using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

using playn.ios;
using playn.core;
using edu.bsu.pvgestwicki.etschallenge.core;

namespace edu.bsu.pvgestwicki.etschallenge
{
  [Register ("AppDelegate")]
  public partial class AppDelegate : UIApplicationDelegate {
    public override bool FinishedLaunching (UIApplication app, NSDictionary options) {
      app.SetStatusBarHidden(true, true);
      IOSPlatform.register(app, IOSPlatform.SupportedOrients.PORTRAITS);
      PlayN.run(new AlgebraGame());
      return true;
    }
  }

  public class Application {
    static void Main (string[] args) {
      UIApplication.Main (args, null, "AppDelegate");
    }
  }
}
