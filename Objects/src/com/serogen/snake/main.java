package com.serogen.snake;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.serogen.snake", "com.serogen.snake.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.serogen.snake", "com.serogen.snake.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.serogen.snake.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _картинка = null;
public static int[] _x = null;
public static int[] _y = null;
public static int _xхвоста = 0;
public static int _yхвоста = 0;
public static int _длиназмейки = 0;
public static boolean _вверх = false;
public static boolean _вниз = false;
public static boolean _влево = false;
public static boolean _вправо = false;
public static String _первыйраз = "";
public static int _i2 = 0;
public static int _xеды = 0;
public static int _yеды = 0;
public anywheresoftware.b4a.objects.LabelWrapper _lb1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbgo = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbpress = null;
public static int _счёт = 0;
public static int _рекорд = 0;
public static int _шрифт = 0;
public static int _шрифт2 = 0;
public static int _поправка = 0;
public anywheresoftware.b4a.objects.ImageViewWrapper _picturebox1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _image = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _canvas = null;
public anywheresoftware.b4a.objects.Timer _timer2 = null;
public anywheresoftware.b4a.objects.Timer _timer1 = null;
public anywheresoftware.b4a.objects.Timer _timer3 = null;
public static float _dx = 0f;
public static float _dy = 0f;
public static boolean _проверкаy = false;
public static String _направление = "";
public static float _шагfl = 0f;
public static int _шаг = 0;
public static int _шаг1 = 0;
public static int _шаг2 = 0;
public static boolean _loose = false;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _mp = null;
public static boolean _игра = false;
public static boolean _можно = false;
public anywheresoftware.b4a.objects.ImageViewWrapper _pause = null;
public static boolean _пауза = false;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 64;BA.debugLine="If ШагFL>Шаг1 Then Шаг=Шаг1 Else Шаг=Шаг2";
if (_шагfl>_шаг1) { 
_шаг = _шаг1;}
else {
_шаг = _шаг2;};
 //BA.debugLineNum = 65;BA.debugLine="If FirstTime=True Then";
if (_firsttime==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 66;BA.debugLine="Activity.LoadLayout(\"Me\")";
mostCurrent._activity.LoadLayout("Me",mostCurrent.activityBA);
 //BA.debugLineNum = 67;BA.debugLine="If Activity.Width  > 700 And Activity.Width  <800";
if (mostCurrent._activity.getWidth()>700 && mostCurrent._activity.getWidth()<800) { 
 //BA.debugLineNum = 68;BA.debugLine="Шрифт = Activity.Width/24";
_шрифт = (int) (mostCurrent._activity.getWidth()/(double)24);
 }else {
 //BA.debugLineNum = 70;BA.debugLine="If Activity.Width  >750 Then";
if (mostCurrent._activity.getWidth()>750) { 
 //BA.debugLineNum = 71;BA.debugLine="Шрифт  = Activity.Width/22";
_шрифт = (int) (mostCurrent._activity.getWidth()/(double)22);
 }else {
 //BA.debugLineNum = 73;BA.debugLine="Шрифт  = Activity.Width/26";
_шрифт = (int) (mostCurrent._activity.getWidth()/(double)26);
 };
 };
 //BA.debugLineNum = 76;BA.debugLine="Вверх = True";
_вверх = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 77;BA.debugLine="Load_high";
_load_high();
 //BA.debugLineNum = 78;BA.debugLine="lb2.Top = 0";
mostCurrent._lb2.setTop((int) (0));
 //BA.debugLineNum = 79;BA.debugLine="lb2.Left = 0";
mostCurrent._lb2.setLeft((int) (0));
 //BA.debugLineNum = 80;BA.debugLine="lb2.Text = \"Score:\"";
mostCurrent._lb2.setText((Object)("Score:"));
 //BA.debugLineNum = 81;BA.debugLine="lb2.TextSize=Шрифт";
mostCurrent._lb2.setTextSize((float) (_шрифт));
 //BA.debugLineNum = 82;BA.debugLine="lb2.Width=25%x";
mostCurrent._lb2.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA));
 //BA.debugLineNum = 83;BA.debugLine="lb2.Height=100%x/22*4";
mostCurrent._lb2.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*4));
 //BA.debugLineNum = 84;BA.debugLine="lb2.Visible = False";
mostCurrent._lb2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 86;BA.debugLine="lb1.Top = 0";
mostCurrent._lb1.setTop((int) (0));
 //BA.debugLineNum = 87;BA.debugLine="lb1.Left = lb2.Width";
mostCurrent._lb1.setLeft(mostCurrent._lb2.getWidth());
 //BA.debugLineNum = 88;BA.debugLine="lb1.Text = \"0\"";
mostCurrent._lb1.setText((Object)("0"));
 //BA.debugLineNum = 89;BA.debugLine="lb1.TextSize=Шрифт";
mostCurrent._lb1.setTextSize((float) (_шрифт));
 //BA.debugLineNum = 90;BA.debugLine="lb1.Width=15%x";
mostCurrent._lb1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA));
 //BA.debugLineNum = 91;BA.debugLine="lb1.Height=100%x/22*4";
mostCurrent._lb1.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*4));
 //BA.debugLineNum = 92;BA.debugLine="lb1.Visible = False";
mostCurrent._lb1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 94;BA.debugLine="lb3.Top = 0";
mostCurrent._lb3.setTop((int) (0));
 //BA.debugLineNum = 95;BA.debugLine="lb3.Left = lb1.Width+lb2.Width+5%x";
mostCurrent._lb3.setLeft((int) (mostCurrent._lb1.getWidth()+mostCurrent._lb2.getWidth()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA)));
 //BA.debugLineNum = 96;BA.debugLine="lb3.Text = \"Best:\"";
mostCurrent._lb3.setText((Object)("Best:"));
 //BA.debugLineNum = 97;BA.debugLine="lb3.TextSize=Шрифт";
mostCurrent._lb3.setTextSize((float) (_шрифт));
 //BA.debugLineNum = 98;BA.debugLine="lb3.Width=33%x";
mostCurrent._lb3.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (33),mostCurrent.activityBA));
 //BA.debugLineNum = 99;BA.debugLine="lb3.Height=100%x/22*4";
mostCurrent._lb3.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*4));
 //BA.debugLineNum = 100;BA.debugLine="lb3.Visible = False";
mostCurrent._lb3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 103;BA.debugLine="lb4.Top = 0";
mostCurrent._lb4.setTop((int) (0));
 //BA.debugLineNum = 104;BA.debugLine="lb4.Left = lb1.Width+lb2.Width+5%x+lb3";
mostCurrent._lb4.setLeft((int) (mostCurrent._lb1.getWidth()+mostCurrent._lb2.getWidth()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA)+mostCurrent._lb3.getWidth()));
 //BA.debugLineNum = 105;BA.debugLine="lb4.Text = Рекорд";
mostCurrent._lb4.setText((Object)(_рекорд));
 //BA.debugLineNum = 106;BA.debugLine="lb4.TextSize=Шрифт";
mostCurrent._lb4.setTextSize((float) (_шрифт));
 //BA.debugLineNum = 107;BA.debugLine="lb4.Width=100%x-(lb1.Width+lb2.Width+5%x+lb3.Wi";
mostCurrent._lb4.setWidth((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-(mostCurrent._lb1.getWidth()+mostCurrent._lb2.getWidth()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA)+mostCurrent._lb3.getWidth())));
 //BA.debugLineNum = 108;BA.debugLine="lb4.Height=100%x/22*4";
mostCurrent._lb4.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*4));
 //BA.debugLineNum = 109;BA.debugLine="lb4.Visible = False";
mostCurrent._lb4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 111;BA.debugLine="lbgo.Top = 100%x/22*6";
mostCurrent._lbgo.setTop((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*6));
 //BA.debugLineNum = 112;BA.debugLine="lbgo.Left = 100%x/22";
mostCurrent._lbgo.setLeft((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22));
 //BA.debugLineNum = 113;BA.debugLine="lbgo.Text = \"GAME OVER\"";
mostCurrent._lbgo.setText((Object)("GAME OVER"));
 //BA.debugLineNum = 114;BA.debugLine="lbgo.TextSize=Шрифт*1.5";
mostCurrent._lbgo.setTextSize((float) (_шрифт*1.5));
 //BA.debugLineNum = 115;BA.debugLine="lbgo.Width=100%x/22*20";
mostCurrent._lbgo.setWidth((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*20));
 //BA.debugLineNum = 116;BA.debugLine="lbgo.Height=100%x/22*6";
mostCurrent._lbgo.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*6));
 //BA.debugLineNum = 117;BA.debugLine="lbgo.Visible = False";
mostCurrent._lbgo.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 119;BA.debugLine="lbpress.Top = 100%x/22*6";
mostCurrent._lbpress.setTop((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*6));
 //BA.debugLineNum = 120;BA.debugLine="lbpress.Left =100%x/22";
mostCurrent._lbpress.setLeft((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22));
 //BA.debugLineNum = 121;BA.debugLine="lbpress.Text = \"Touch the screen\"";
mostCurrent._lbpress.setText((Object)("Touch the screen"));
 //BA.debugLineNum = 122;BA.debugLine="lbpress.TextSize=Шрифт";
mostCurrent._lbpress.setTextSize((float) (_шрифт));
 //BA.debugLineNum = 123;BA.debugLine="lbpress.Width=100%x/22*19";
mostCurrent._lbpress.setWidth((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*19));
 //BA.debugLineNum = 124;BA.debugLine="lbpress.Height=100%x/22*8";
mostCurrent._lbpress.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22*8));
 //BA.debugLineNum = 125;BA.debugLine="lbpress.Visible = False";
mostCurrent._lbpress.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="Image.Width = 100%x";
mostCurrent._image.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 128;BA.debugLine="Image.Height = 100%y";
mostCurrent._image.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 129;BA.debugLine="Image.Top = 0";
mostCurrent._image.setTop((int) (0));
 //BA.debugLineNum = 130;BA.debugLine="Image.Left = 0";
mostCurrent._image.setLeft((int) (0));
 //BA.debugLineNum = 132;BA.debugLine="PictureBox1.Width = 100%x";
mostCurrent._picturebox1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 133;BA.debugLine="PictureBox1.Height = 100%x";
mostCurrent._picturebox1.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 134;BA.debugLine="PictureBox1.Top = (100%y-PictureBox1.Height)/";
mostCurrent._picturebox1.setTop((int) ((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-mostCurrent._picturebox1.getHeight())/(double)2));
 //BA.debugLineNum = 135;BA.debugLine="PictureBox1.Left = 0";
mostCurrent._picturebox1.setLeft((int) (0));
 //BA.debugLineNum = 137;BA.debugLine="Button1 .Width=75%x";
mostCurrent._button1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (75),mostCurrent.activityBA));
 //BA.debugLineNum = 138;BA.debugLine="Button1.Height=10%y";
mostCurrent._button1.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 139;BA.debugLine="Button1.Top=55%y";
mostCurrent._button1.setTop(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (55),mostCurrent.activityBA));
 //BA.debugLineNum = 140;BA.debugLine="Button1.TextSize=Шрифт";
mostCurrent._button1.setTextSize((float) (_шрифт));
 //BA.debugLineNum = 141;BA.debugLine="Button1.Color=Colors.RGB(127,255,0)";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (127),(int) (255),(int) (0)));
 //BA.debugLineNum = 142;BA.debugLine="Button1.Left=50%x-Button1.Width/2";
mostCurrent._button1.setLeft((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-mostCurrent._button1.getWidth()/(double)2));
 //BA.debugLineNum = 145;BA.debugLine="Timer2.Initialize(\"Timer2\", 3000)";
mostCurrent._timer2.Initialize(processBA,"Timer2",(long) (3000));
 //BA.debugLineNum = 146;BA.debugLine="Timer2.Enabled=True";
mostCurrent._timer2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 147;BA.debugLine="Timer3.Initialize(\"Timer3\", 50)";
mostCurrent._timer3.Initialize(processBA,"Timer3",(long) (50));
 //BA.debugLineNum = 148;BA.debugLine="Timer3.Enabled=True";
mostCurrent._timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 149;BA.debugLine="Timer1.Initialize(\"Timer1\", 150)";
mostCurrent._timer1.Initialize(processBA,"Timer1",(long) (150));
 //BA.debugLineNum = 150;BA.debugLine="Timer1.Enabled=False";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 152;BA.debugLine="Pause.Width=100%y-(((100%y-(Шаг*28))/2)+(Шаг*28";
mostCurrent._pause.setWidth((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*28))));
 //BA.debugLineNum = 153;BA.debugLine="Pause.Height=100%y-(((100%y-(Шаг*28))/2)+(Шаг*2";
mostCurrent._pause.setHeight((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*28))));
 //BA.debugLineNum = 154;BA.debugLine="Pause.Left=50%x-(100%y-(((100%y-(Шаг*28))/2)+(Ш";
mostCurrent._pause.setLeft((int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*28)))/(double)2));
 //BA.debugLineNum = 155;BA.debugLine="Pause.Top=((100%y-(Шаг*28))/2)+(Шаг*28)";
mostCurrent._pause.setTop((int) (((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*28)));
 //BA.debugLineNum = 157;BA.debugLine="MP.Initialize2(\"MP\")";
mostCurrent._mp.Initialize2(processBA,"MP");
 };
 //BA.debugLineNum = 159;BA.debugLine="FirstTime=False";
_firsttime = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 166;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
return "";
}
public static String  _activity_touch(int _action,float _xкас,float _yкас) throws Exception{
 //BA.debugLineNum = 231;BA.debugLine="Sub Activity_Touch (Action As Int, Xкас As Float,";
 //BA.debugLineNum = 232;BA.debugLine="If Игра=True Then";
if (_игра==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 233;BA.debugLine="If Можно=True Then";
if (_можно==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 234;BA.debugLine="Xкас=Round(Xкас)";
_xкас = (float) (anywheresoftware.b4a.keywords.Common.Round(_xкас));
 //BA.debugLineNum = 235;BA.debugLine="Yкас=Round(Yкас)";
_yкас = (float) (anywheresoftware.b4a.keywords.Common.Round(_yкас));
 //BA.debugLineNum = 237;BA.debugLine="Select Action";
switch (_action) {
case 2: {
 //BA.debugLineNum = 239;BA.debugLine="If ПервыйРаз=False Then";
if ((mostCurrent._первыйраз).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False))) { 
 //BA.debugLineNum = 240;BA.debugLine="If Dx<>Xкас And Dy<>Yкас Then";
if (_dx!=_xкас && _dy!=_yкас) { 
 //BA.debugLineNum = 241;BA.debugLine="If Xкас<Dx And (Dx-Xкас>Yкас-Dy And Dx-Xкас>Dy-Y";
if (_xкас<_dx && (_dx-_xкас>_yкас-_dy && _dx-_xкас>_dy-_yкас)) { 
mostCurrent._направление = "Left";};
 //BA.debugLineNum = 242;BA.debugLine="If Xкас>Dx And (Xкас-Dx>Yкас-Dy And Xкас-Dx>Dy-Y";
if (_xкас>_dx && (_xкас-_dx>_yкас-_dy && _xкас-_dx>_dy-_yкас)) { 
mostCurrent._направление = "Right";};
 //BA.debugLineNum = 243;BA.debugLine="If Yкас<Dy And (Dy-Yкас>Xкас-Dx And Dy-Yкас>Dx-X";
if (_yкас<_dy && (_dy-_yкас>_xкас-_dx && _dy-_yкас>_dx-_xкас)) { 
mostCurrent._направление = "Up";};
 //BA.debugLineNum = 244;BA.debugLine="If Yкас>Dy And (Yкас-Dy>Xкас-Dx And Yкас-Dy>Dx-X";
if (_yкас>_dy && (_yкас-_dy>_xкас-_dx && _yкас-_dy>_dx-_xкас)) { 
mostCurrent._направление = "Down";};
 };
 };
 break; }
case 0: {
 //BA.debugLineNum = 248;BA.debugLine="If ПервыйРаз Then Timer1.Enabled = True";
if (BA.ObjectToBoolean(mostCurrent._первыйраз)) { 
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);};
 break; }
}
;
 //BA.debugLineNum = 251;BA.debugLine="Dx=Xкас";
_dx = _xкас;
 //BA.debugLineNum = 252;BA.debugLine="Dy=Yкас";
_dy = _yкас;
 //BA.debugLineNum = 255;BA.debugLine="If ДлинаЗмейки<>1 And ((Направление = \"Up\" And";
if (_длиназмейки!=1 && (((mostCurrent._направление).equals("Up") && _вниз==anywheresoftware.b4a.keywords.Common.True) || ((mostCurrent._направление).equals("Down") && _вверх==anywheresoftware.b4a.keywords.Common.True) || ((mostCurrent._направление).equals("Left") && _вправо==anywheresoftware.b4a.keywords.Common.True) || ((mostCurrent._направление).equals("Right") && _влево==anywheresoftware.b4a.keywords.Common.True))) { 
 //BA.debugLineNum = 256;BA.debugLine="Шаг=100%x/22";
_шаг = (int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22);
 }else {
 //BA.debugLineNum = 258;BA.debugLine="Select Направление";
switch (BA.switchObjectToInt(mostCurrent._направление,"Up","Down","Left","Right")) {
case 0: {
 //BA.debugLineNum = 259;BA.debugLine="Case \"Up\" : Вверх = True : Вниз = False : Влев";
_вверх = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 259;BA.debugLine="Case \"Up\" : Вверх = True : Вниз = False : Влев";
_вниз = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 259;BA.debugLine="Case \"Up\" : Вверх = True : Вниз = False : Влев";
_влево = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 259;BA.debugLine="Case \"Up\" : Вверх = True : Вниз = False : Влев";
_вправо = anywheresoftware.b4a.keywords.Common.False;
 break; }
case 1: {
 //BA.debugLineNum = 260;BA.debugLine="Case \"Down\" : Вверх = False : Вниз = True : Вл";
_вверх = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 260;BA.debugLine="Case \"Down\" : Вверх = False : Вниз = True : Вл";
_вниз = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 260;BA.debugLine="Case \"Down\" : Вверх = False : Вниз = True : Вл";
_влево = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 260;BA.debugLine="Case \"Down\" : Вверх = False : Вниз = True : Вл";
_вправо = anywheresoftware.b4a.keywords.Common.False;
 break; }
case 2: {
 //BA.debugLineNum = 261;BA.debugLine="Case \"Left\" : Вверх = False : Вниз = False : В";
_вверх = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 261;BA.debugLine="Case \"Left\" : Вверх = False : Вниз = False : В";
_вниз = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 261;BA.debugLine="Case \"Left\" : Вверх = False : Вниз = False : В";
_влево = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 261;BA.debugLine="Case \"Left\" : Вверх = False : Вниз = False : В";
_вправо = anywheresoftware.b4a.keywords.Common.False;
 break; }
case 3: {
 //BA.debugLineNum = 262;BA.debugLine="Case \"Right\" : Вверх = False : Вниз = False :";
_вверх = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 262;BA.debugLine="Case \"Right\" : Вверх = False : Вниз = False :";
_вниз = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 262;BA.debugLine="Case \"Right\" : Вверх = False : Вниз = False :";
_влево = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 262;BA.debugLine="Case \"Right\" : Вверх = False : Вниз = False :";
_вправо = anywheresoftware.b4a.keywords.Common.True;
 break; }
}
;
 };
 //BA.debugLineNum = 265;BA.debugLine="Можно=False";
_можно = anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 268;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 178;BA.debugLine="Sub Button1_click";
 //BA.debugLineNum = 179;BA.debugLine="Start_again";
_start_again();
 //BA.debugLineNum = 181;BA.debugLine="MP.Stop";
mostCurrent._mp.Stop();
 //BA.debugLineNum = 182;BA.debugLine="MP.Initialize2(\"MP\")";
mostCurrent._mp.Initialize2(processBA,"MP");
 //BA.debugLineNum = 183;BA.debugLine="MP.Load(File.DirAssets,\"";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"rattle.wav");
 //BA.debugLineNum = 184;BA.debugLine="MP.Play";
mostCurrent._mp.Play();
 //BA.debugLineNum = 188;BA.debugLine="Игра=True";
_игра = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 189;BA.debugLine="End Sub";
return "";
}
public static String  _food() throws Exception{
int _i = 0;
 //BA.debugLineNum = 400;BA.debugLine="Sub Food";
 //BA.debugLineNum = 401;BA.debugLine="xЕды =Шаг*(Round(Rnd(1,22)))";
_xеды = (int) (_шаг*(anywheresoftware.b4a.keywords.Common.Round(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (22)))));
 //BA.debugLineNum = 402;BA.debugLine="yЕды =100%y- Шаг*(Round(Rnd(1,50)))";
_yеды = (int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-_шаг*(anywheresoftware.b4a.keywords.Common.Round(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (50)))));
 //BA.debugLineNum = 404;BA.debugLine="If xЕды < (Шаг*2)Or yЕды < ((100%y-(Шаг*28))/2) Or";
if (_xеды<(_шаг*2) || _yеды<((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2) || _xеды>(_шаг*19) || _yеды>((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*27)) { 
_food();};
 //BA.debugLineNum = 406;BA.debugLine="For i = 0 To ДлинаЗмейки";
{
final int step4 = 1;
final int limit4 = _длиназмейки;
for (_i = (int) (0) ; (step4 > 0 && _i <= limit4) || (step4 < 0 && _i >= limit4); _i = ((int)(0 + _i + step4)) ) {
 //BA.debugLineNum = 407;BA.debugLine="If((x(i)+100%x/44 > xЕды) And (x(i)-10";
if (((_x[_i]+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44>_xеды) && (_x[_i]-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44<_xеды)) && ((_y[_i]+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44>_yеды) && (_y[_i]-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44<_yеды))) { 
_food();};
 }
};
 //BA.debugLineNum = 410;BA.debugLine="Canvas.DrawCircle(xЕды+Шаг/2,yЕды+Шаг/2,Шаг/2,Colo";
mostCurrent._canvas.DrawCircle((float) (_xеды+_шаг/(double)2),(float) (_yеды+_шаг/(double)2),(float) (_шаг/(double)2),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (139),(int) (0),(int) (0)),anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))));
 //BA.debugLineNum = 413;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 415;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim Картинка As Canvas";
mostCurrent._картинка = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim x(641), y(641) As Int";
_x = new int[(int) (641)];
;
_y = new int[(int) (641)];
;
 //BA.debugLineNum = 23;BA.debugLine="Dim xХвоста, yХвоста As Int";
_xхвоста = 0;
_yхвоста = 0;
 //BA.debugLineNum = 24;BA.debugLine="Dim ДлинаЗмейки As Int = 1";
_длиназмейки = (int) (1);
 //BA.debugLineNum = 25;BA.debugLine="Dim Вверх, Вниз, Влево, Вправо As Boolean";
_вверх = false;
_вниз = false;
_влево = false;
_вправо = false;
 //BA.debugLineNum = 26;BA.debugLine="Dim ПервыйРаз = True";
mostCurrent._первыйраз = BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 27;BA.debugLine="Dim i2 As Int";
_i2 = 0;
 //BA.debugLineNum = 28;BA.debugLine="Dim xЕды As Int";
_xеды = 0;
 //BA.debugLineNum = 29;BA.debugLine="Dim yЕды As Int";
_yеды = 0;
 //BA.debugLineNum = 30;BA.debugLine="Dim lb1 As Label";
mostCurrent._lb1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim lb2 As Label";
mostCurrent._lb2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim lb3 As Label";
mostCurrent._lb3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim lb4 As Label";
mostCurrent._lb4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim lbgo As Label";
mostCurrent._lbgo = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim lbpress As Label";
mostCurrent._lbpress = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim Счёт As Int";
_счёт = 0;
 //BA.debugLineNum = 37;BA.debugLine="Dim Рекорд As Int";
_рекорд = 0;
 //BA.debugLineNum = 38;BA.debugLine="Dim Шрифт As Int";
_шрифт = 0;
 //BA.debugLineNum = 39;BA.debugLine="Dim Шрифт2 As Int";
_шрифт2 = 0;
 //BA.debugLineNum = 40;BA.debugLine="Dim Поправка As Int";
_поправка = 0;
 //BA.debugLineNum = 41;BA.debugLine="Dim PictureBox1 As ImageView";
mostCurrent._picturebox1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Dim Image As ImageView";
mostCurrent._image = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Dim Canvas As Canvas";
mostCurrent._canvas = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Dim Timer2 As Timer";
mostCurrent._timer2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 46;BA.debugLine="Dim Timer1 As Timer";
mostCurrent._timer1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 47;BA.debugLine="Dim Timer3 As Timer";
mostCurrent._timer3 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 48;BA.debugLine="Dim Dx, Dy As Float";
_dx = 0f;
_dy = 0f;
 //BA.debugLineNum = 49;BA.debugLine="Dim ПроверкаY As Boolean=False";
_проверкаy = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 50;BA.debugLine="Dim Направление As String";
mostCurrent._направление = "";
 //BA.debugLineNum = 51;BA.debugLine="Dim ШагFL As Float=100%x/22";
_шагfl = (float) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22);
 //BA.debugLineNum = 52;BA.debugLine="Dim Шаг As Int";
_шаг = 0;
 //BA.debugLineNum = 53;BA.debugLine="Dim Шаг1 As Int=100%x/22";
_шаг1 = (int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22);
 //BA.debugLineNum = 54;BA.debugLine="Dim Шаг2 As Int=Round(100%x/22)";
_шаг2 = (int) (anywheresoftware.b4a.keywords.Common.Round(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)22));
 //BA.debugLineNum = 55;BA.debugLine="Dim Loose As Boolean";
_loose = false;
 //BA.debugLineNum = 56;BA.debugLine="Dim MP As MediaPlayer";
mostCurrent._mp = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Dim Игра As Boolean=False";
_игра = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 58;BA.debugLine="Dim Можно As Boolean=True";
_можно = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 59;BA.debugLine="Dim Pause As ImageView";
mostCurrent._pause = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Dim Пауза As Boolean=False";
_пауза = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _load_high() throws Exception{
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
 //BA.debugLineNum = 417;BA.debugLine="Sub Load_high";
 //BA.debugLineNum = 418;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 419;BA.debugLine="If File.Exists(File.DirDefaultExternal,\"High.t";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"High.txt")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 420;BA.debugLine="File.Copy(File.DirAssets,\"High.txt\",File.DirDe";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"High.txt",anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"High.txt");
 };
 //BA.debugLineNum = 422;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.Dir";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"High.txt").getObject()));
 //BA.debugLineNum = 423;BA.debugLine="Рекорд=TextReader1.ReadLine";
_рекорд = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 424;BA.debugLine="End Sub";
return "";
}
public static String  _lose() throws Exception{
 //BA.debugLineNum = 353;BA.debugLine="Sub Lose";
 //BA.debugLineNum = 354;BA.debugLine="Игра =False";
_игра = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 355;BA.debugLine="Save_high";
_save_high();
 //BA.debugLineNum = 356;BA.debugLine="Load_high";
_load_high();
 //BA.debugLineNum = 357;BA.debugLine="lbgo.Visible = True";
mostCurrent._lbgo.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 358;BA.debugLine="Button1.Visible = True";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 359;BA.debugLine="Pause.Visible=False";
mostCurrent._pause.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 360;BA.debugLine="Button1.Enabled = True";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 361;BA.debugLine="Timer1.Enabled = False";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 362;BA.debugLine="Loose=True";
_loose = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 364;BA.debugLine="MP.Stop";
mostCurrent._mp.Stop();
 //BA.debugLineNum = 365;BA.debugLine="MP.Initialize2(\"MP\")";
mostCurrent._mp.Initialize2(processBA,"MP");
 //BA.debugLineNum = 366;BA.debugLine="MP.Load(File.DirAssets,\"ping.wav\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ping.wav");
 //BA.debugLineNum = 367;BA.debugLine="MP.Play";
mostCurrent._mp.Play();
 //BA.debugLineNum = 371;BA.debugLine="End Sub";
return "";
}
public static String  _nyam() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect1 = null;
 //BA.debugLineNum = 373;BA.debugLine="Sub nyam";
 //BA.debugLineNum = 374;BA.debugLine="ДлинаЗмейки = ДлинаЗмейки+1";
_длиназмейки = (int) (_длиназмейки+1);
 //BA.debugLineNum = 375;BA.debugLine="Счёт =Счёт+ 1";
_счёт = (int) (_счёт+1);
 //BA.debugLineNum = 376;BA.debugLine="lb1.Text = Счёт";
mostCurrent._lb1.setText((Object)(_счёт));
 //BA.debugLineNum = 377;BA.debugLine="Dim Rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 378;BA.debugLine="Rect1.Initialize(x(0),y(0),x(0)+Шаг,y(0)+Шаг)";
_rect1.Initialize(_x[(int) (0)],_y[(int) (0)],(int) (_x[(int) (0)]+_шаг),(int) (_y[(int) (0)]+_шаг));
 //BA.debugLineNum = 379;BA.debugLine="Canvas.DrawRect(Rect1,Colors.RGB(143,188,139),True";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (143),(int) (188),(int) (139)),anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 381;BA.debugLine="Dim Rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 382;BA.debugLine="Rect1.Initialize(x(0)  + Шаг/720,y(0) + Шаг/720,x(";
_rect1.Initialize((int) (_x[(int) (0)]+_шаг/(double)720),(int) (_y[(int) (0)]+_шаг/(double)720),(int) (_x[(int) (0)]+_шаг/(double)720+_шаг-_шаг/(double)360),(int) (_y[(int) (0)]+_шаг/(double)720+_шаг-_шаг/(double)360));
 //BA.debugLineNum = 383;BA.debugLine="Canvas.DrawRect(Rect1,Colors.Black,True, 1dip )";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Black,anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 384;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 389;BA.debugLine="MP.Stop";
mostCurrent._mp.Stop();
 //BA.debugLineNum = 390;BA.debugLine="MP.Initialize2(\"MP\")";
mostCurrent._mp.Initialize2(processBA,"MP");
 //BA.debugLineNum = 391;BA.debugLine="MP.Load(File.DirAssets,\"mort08.wav\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"mort08.wav");
 //BA.debugLineNum = 392;BA.debugLine="MP.Play";
mostCurrent._mp.Play();
 //BA.debugLineNum = 397;BA.debugLine="Food";
_food();
 //BA.debugLineNum = 398;BA.debugLine="End Sub";
return "";
}
public static String  _pause_click() throws Exception{
 //BA.debugLineNum = 435;BA.debugLine="Sub Pause_Click";
 //BA.debugLineNum = 436;BA.debugLine="If Пауза=False Then";
if (_пауза==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 437;BA.debugLine="Timer1.Enabled=False";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 438;BA.debugLine="lbpress.Visible=True";
mostCurrent._lbpress.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 439;BA.debugLine="lbpress.text=\"PAUSE\"";
mostCurrent._lbpress.setText((Object)("PAUSE"));
 //BA.debugLineNum = 440;BA.debugLine="Пауза=Not(Пауза)";
_пауза = anywheresoftware.b4a.keywords.Common.Not(_пауза);
 }else {
 //BA.debugLineNum = 442;BA.debugLine="Timer1.Enabled=True";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 443;BA.debugLine="lbpress.Visible=False";
mostCurrent._lbpress.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 444;BA.debugLine="lbpress.text=\"Touch the screen\"";
mostCurrent._lbpress.setText((Object)("Touch the screen"));
 //BA.debugLineNum = 445;BA.debugLine="Пауза=Not(Пауза)";
_пауза = anywheresoftware.b4a.keywords.Common.Not(_пауза);
 };
 //BA.debugLineNum = 447;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static String  _save_high() throws Exception{
anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _textwriter1 = null;
 //BA.debugLineNum = 425;BA.debugLine="Sub Save_high";
 //BA.debugLineNum = 426;BA.debugLine="Dim TextWriter1 As TextWriter";
_textwriter1 = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 427;BA.debugLine="If Счёт > Рекорд Then";
if (_счёт>_рекорд) { 
 //BA.debugLineNum = 428;BA.debugLine="TextWriter1.Initialize(File.OpenOutput(File.Di";
_textwriter1.Initialize((java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"High.txt",anywheresoftware.b4a.keywords.Common.False).getObject()));
 //BA.debugLineNum = 429;BA.debugLine="TextWriter1.WriteLine(Счёт)";
_textwriter1.WriteLine(BA.NumberToString(_счёт));
 //BA.debugLineNum = 430;BA.debugLine="TextWriter1.Close";
_textwriter1.Close();
 };
 //BA.debugLineNum = 432;BA.debugLine="End Sub";
return "";
}
public static String  _start_again() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect1 = null;
 //BA.debugLineNum = 190;BA.debugLine="Sub Start_again";
 //BA.debugLineNum = 192;BA.debugLine="Loose=False";
_loose = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 193;BA.debugLine="Image.Visible=False";
mostCurrent._image.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 195;BA.debugLine="Вверх = True : Вниз = False : Влево = Fals";
_вверх = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 195;BA.debugLine="Вверх = True : Вниз = False : Влево = Fals";
_вниз = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 195;BA.debugLine="Вверх = True : Вниз = False : Влево = Fals";
_влево = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 195;BA.debugLine="Вверх = True : Вниз = False : Влево = Fals";
_вправо = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 197;BA.debugLine="lb1.Visible = False";
mostCurrent._lb1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 198;BA.debugLine="lb2.Visible = False";
mostCurrent._lb2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 199;BA.debugLine="lb3.Visible = False";
mostCurrent._lb3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 200;BA.debugLine="lb4.Visible = False";
mostCurrent._lb4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 201;BA.debugLine="Pause.Visible=False";
mostCurrent._pause.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 202;BA.debugLine="Счёт = 0";
_счёт = (int) (0);
 //BA.debugLineNum = 204;BA.debugLine="lb1.Text = Счёт";
mostCurrent._lb1.setText((Object)(_счёт));
 //BA.debugLineNum = 206;BA.debugLine="Load_high";
_load_high();
 //BA.debugLineNum = 207;BA.debugLine="lb4.Text = Рекорд";
mostCurrent._lb4.setText((Object)(_рекорд));
 //BA.debugLineNum = 209;BA.debugLine="ДлинаЗмейки = 1";
_длиназмейки = (int) (1);
 //BA.debugLineNum = 210;BA.debugLine="ПервыйРаз = True";
mostCurrent._первыйраз = BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 212;BA.debugLine="Canvas.Initialize(Activity)";
mostCurrent._canvas.Initialize((android.view.View)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 214;BA.debugLine="Dim Rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 215;BA.debugLine="Rect1.Initialize(0,0,100%x,100%y)";
_rect1.Initialize((int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 216;BA.debugLine="Canvas.DrawRect(Rect1,Colors.RGB(189,183,107),True";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (189),(int) (183),(int) (107)),anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 218;BA.debugLine="Dim Rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 219;BA.debugLine="Rect1.Initialize((Шаг*2),((Round(100%y/Шаг)*Шаг)-(";
_rect1.Initialize((int) ((_шаг*2)),(int) (((anywheresoftware.b4a.keywords.Common.Round(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)/(double)_шаг)*_шаг)-(_шаг*28))/(double)2),(int) ((_шаг*20)),(int) (((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*28)));
 //BA.debugLineNum = 220;BA.debugLine="Canvas.DrawRect(Rect1,Colors.RGB(143,188,139),True";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (143),(int) (188),(int) (139)),anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 222;BA.debugLine="y(0) = 100%y/2";
_y[(int) (0)] = (int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)/(double)2);
 //BA.debugLineNum = 223;BA.debugLine="x(0) = Шаг*11";
_x[(int) (0)] = (int) (_шаг*11);
 //BA.debugLineNum = 224;BA.debugLine="lbpress.Visible = True";
mostCurrent._lbpress.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 225;BA.debugLine="lbgo.Visible = False";
mostCurrent._lbgo.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 227;BA.debugLine="Button1.Visible = False";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 229;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
int _i = 0;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect1 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect2 = null;
 //BA.debugLineNum = 270;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 275;BA.debugLine="lb1.Visible = True";
mostCurrent._lb1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 276;BA.debugLine="lb2.Visible = True";
mostCurrent._lb2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 277;BA.debugLine="lb3.Visible = True";
mostCurrent._lb3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 278;BA.debugLine="lb4.Visible = True";
mostCurrent._lb4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 279;BA.debugLine="Pause.Visible=True";
mostCurrent._pause.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 280;BA.debugLine="lbpress.Visible = False";
mostCurrent._lbpress.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 281;BA.debugLine="If ПервыйРаз Then";
if (BA.ObjectToBoolean(mostCurrent._первыйраз)) { 
 //BA.debugLineNum = 282;BA.debugLine="xХвоста = Шаг*11";
_xхвоста = (int) (_шаг*11);
 //BA.debugLineNum = 283;BA.debugLine="yХвоста = 100%y/2";
_yхвоста = (int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)/(double)2);
 //BA.debugLineNum = 284;BA.debugLine="Food";
_food();
 }else {
 //BA.debugLineNum = 286;BA.debugLine="xХвоста = x(ДлинаЗмейки)";
_xхвоста = _x[_длиназмейки];
 //BA.debugLineNum = 287;BA.debugLine="yХвоста = y(ДлинаЗмейки)";
_yхвоста = _y[_длиназмейки];
 };
 //BA.debugLineNum = 290;BA.debugLine="If Влево = True Then x(0) = x(0) - Шаг";
if (_влево==anywheresoftware.b4a.keywords.Common.True) { 
_x[(int) (0)] = (int) (_x[(int) (0)]-_шаг);};
 //BA.debugLineNum = 291;BA.debugLine="If Вверх = True Then y(0) = y(0) - Шаг";
if (_вверх==anywheresoftware.b4a.keywords.Common.True) { 
_y[(int) (0)] = (int) (_y[(int) (0)]-_шаг);};
 //BA.debugLineNum = 292;BA.debugLine="If Вправо = True Then x(0) = x(0) + Шаг";
if (_вправо==anywheresoftware.b4a.keywords.Common.True) { 
_x[(int) (0)] = (int) (_x[(int) (0)]+_шаг);};
 //BA.debugLineNum = 293;BA.debugLine="If Вниз = True Then y(0) = y(0) + Шаг";
if (_вниз==anywheresoftware.b4a.keywords.Common.True) { 
_y[(int) (0)] = (int) (_y[(int) (0)]+_шаг);};
 //BA.debugLineNum = 295;BA.debugLine="If ДлинаЗмейки > 4 Then";
if (_длиназмейки>4) { 
 //BA.debugLineNum = 296;BA.debugLine="For i = 1 To ДлинаЗмейки";
{
final int step20 = 1;
final int limit20 = _длиназмейки;
for (_i = (int) (1) ; (step20 > 0 && _i <= limit20) || (step20 < 0 && _i >= limit20); _i = ((int)(0 + _i + step20)) ) {
 //BA.debugLineNum = 297;BA.debugLine="If x(0) = x(i) And y(0) = y(i) The";
if (_x[(int) (0)]==_x[_i] && _y[(int) (0)]==_y[_i]) { 
_lose();};
 }
};
 };
 //BA.debugLineNum = 300;BA.debugLine="For i = ДлинаЗмейки To 1 Step -1";
{
final int step24 = (int) (-1);
final int limit24 = (int) (1);
for (_i = _длиназмейки ; (step24 > 0 && _i <= limit24) || (step24 < 0 && _i >= limit24); _i = ((int)(0 + _i + step24)) ) {
 //BA.debugLineNum = 301;BA.debugLine="i2 = i - 1";
_i2 = (int) (_i-1);
 //BA.debugLineNum = 302;BA.debugLine="x(i) = x(i2)";
_x[_i] = _x[_i2];
 //BA.debugLineNum = 303;BA.debugLine="y(i) = y(i2)";
_y[_i] = _y[_i2];
 }
};
 //BA.debugLineNum = 309;BA.debugLine="If x(0) < (Шаг*2)Or y(0) < ((Round(100%y/Шаг)*Шаг)";
if (_x[(int) (0)]<(_шаг*2) || _y[(int) (0)]<((anywheresoftware.b4a.keywords.Common.Round(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)/(double)_шаг)*_шаг)-(_шаг*28))/(double)2 || _x[(int) (0)]>(_шаг*19) || _y[(int) (0)]>((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-(_шаг*28))/(double)2)+(_шаг*27)) { 
_lose();};
 //BA.debugLineNum = 312;BA.debugLine="If Loose=False Then";
if (_loose==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 317;BA.debugLine="Dim Rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 318;BA.debugLine="Rect1.Initialize(xХвоста,yХвоста,xХвоста+Шаг,yХвос";
_rect1.Initialize(_xхвоста,_yхвоста,(int) (_xхвоста+_шаг),(int) (_yхвоста+_шаг));
 //BA.debugLineNum = 319;BA.debugLine="Canvas.DrawRect(Rect1,Colors.RGB(143,188,139),True";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (143),(int) (188),(int) (139)),anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 321;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 323;BA.debugLine="Dim Rect2 As Rect";
_rect2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 324;BA.debugLine="Rect2.Initialize(x(0)+Шаг/720 ,y(0)+Шаг/720,x(0)+Ш";
_rect2.Initialize((int) (_x[(int) (0)]+_шаг/(double)720),(int) (_y[(int) (0)]+_шаг/(double)720),(int) (_x[(int) (0)]+_шаг/(double)720+_шаг-_шаг/(double)360),(int) (_y[(int) (0)]+_шаг/(double)720+_шаг-_шаг/(double)360));
 //BA.debugLineNum = 325;BA.debugLine="Canvas.DrawRect(Rect2,Colors.Black,True, 1dip )";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect2.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Black,anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 327;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 };
 //BA.debugLineNum = 330;BA.debugLine="Dim Rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 332;BA.debugLine="Rect1.Initialize(0,0, Шаг,Шаг)";
_rect1.Initialize((int) (0),(int) (0),_шаг,_шаг);
 //BA.debugLineNum = 333;BA.debugLine="Canvas.DrawRect(Rect1,Colors.RGB(189,183,107),True";
mostCurrent._canvas.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (189),(int) (183),(int) (107)),anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 347;BA.debugLine="ПервыйРаз = False";
mostCurrent._первыйраз = BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 349;BA.debugLine="If ((x(0)+100%x/44 > xЕды) And (x(0)-100%x";
if (((_x[(int) (0)]+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44>_xеды) && (_x[(int) (0)]-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44<_xеды)) && ((_y[(int) (0)]+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44>_yеды) && (_y[(int) (0)]-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)/(double)44<_yеды))) { 
_nyam();};
 //BA.debugLineNum = 351;BA.debugLine="End Sub";
return "";
}
public static String  _timer2_tick() throws Exception{
 //BA.debugLineNum = 170;BA.debugLine="Sub Timer2_Tick";
 //BA.debugLineNum = 171;BA.debugLine="PictureBox1.Visible = False";
mostCurrent._picturebox1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 172;BA.debugLine="Image.Visible=True";
mostCurrent._image.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 173;BA.debugLine="Timer2.Enabled = False";
mostCurrent._timer2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 174;BA.debugLine="Activity.Color=Colors.RGB(143,188,139)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (143),(int) (188),(int) (139)));
 //BA.debugLineNum = 175;BA.debugLine="Button1.Visible=True";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _timer3_tick() throws Exception{
 //BA.debugLineNum = 449;BA.debugLine="Sub Timer3_Tick";
 //BA.debugLineNum = 450;BA.debugLine="Можно=True";
_можно = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 451;BA.debugLine="End Sub";
return "";
}
}
