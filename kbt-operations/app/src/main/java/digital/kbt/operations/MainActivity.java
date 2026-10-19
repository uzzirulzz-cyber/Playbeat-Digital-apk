package digital.kbt.operations;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.content.res.ColorStateList;
import android.text.TextUtils;

public class MainActivity extends Activity {
    private final int BG=0xff080D19, PANEL=0xff141F32, MUTED=0xffA4B4CB, WHITE=0xffF5F8FF, GOLD=0xffF5BB4D;
    private LinearLayout root,body;
    private int dp(float n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    private TextView text(String t,int size,int color,boolean bold){ TextView x=new TextView(this);x.setText(t);x.setTextColor(color);x.setTextSize(size);if(bold)x.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return x;}
    private void open(String url){startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
    private void gap(LinearLayout in,int n){View v=new View(this);in.addView(v,new LinearLayout.LayoutParams(1,dp(n)));}
    private void section(String title,String subtitle){TextView h=text(title,19,WHITE,true);body.addView(h); TextView d=text(subtitle,13,MUTED,false);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(4);body.addView(d,p);gap(body,14);}
    private void item(String icon,String name,String detail,String url){
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.HORIZONTAL);card.setGravity(Gravity.CENTER_VERTICAL);card.setPadding(dp(16),dp(18),dp(14),dp(18));card.setBackground(shape(PANEL,16));
        TextView pict=text(icon,24,GOLD,true);pict.setGravity(Gravity.CENTER);card.addView(pict,new LinearLayout.LayoutParams(dp(40),dp(40)));
        LinearLayout labels=new LinearLayout(this);labels.setOrientation(1);TextView n=text(name,16,WHITE,true);labels.addView(n);TextView d=text(detail,12,MUTED,false);d.setMaxLines(2);d.setEllipsize(TextUtils.TruncateAt.END);labels.addView(d);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.leftMargin=dp(10);card.addView(labels,lp);
        TextView arr=text("↗",24,GOLD,true);card.addView(arr);
        card.setOnClickListener(v->open(url));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(10);body.addView(card,cp);
    }
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
        root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(20),dp(27),dp(20),dp(32));scroll.addView(root);setContentView(scroll);
        TextView eyebrow=text("KBT  /  OPERATIONS",13,GOLD,true);eyebrow.setLetterSpacing(.15f);root.addView(eyebrow);gap(root,12);
        TextView title=text("Your command center.",30,WHITE,true);root.addView(title);gap(root,7);
        TextView intro=text("Codex, Work, source control, deployments and cloud files — one mobile dashboard.",14,MUTED,false);root.addView(intro);gap(root,25);
        body=root;
        TextView notice=text("SECURE SIGN-IN  •  Each service opens its official authentication page. KBT never requests or stores your passwords.",12,MUTED,false);notice.setPadding(dp(14),dp(13),dp(14),dp(13));notice.setBackground(shape(0xff17263D,12));root.addView(notice);gap(root,26);
        section("AI workspace","Official ChatGPT and Codex services");
        item("✦","ChatGPT Work","Open Work in your ChatGPT account","https://chatgpt.com/");
        item("⌘","Codex","Coding tasks and repositories","https://chatgpt.com/codex");
        section("DevOps","Authorized service dashboards");
        item("⌥","GitHub","Repositories, pull requests and Actions","https://github.com/");
        item("☁","Cloudflare","Workers, domains, DNS and logs","https://dash.cloudflare.com/");
        item("▲","Vercel","Projects, builds and deployments","https://vercel.com/dashboard");
        section("Cloud files","Sign in with your own service account");
        item("▣","Google Drive","Documents, files and folders","https://drive.google.com/");
        item("◆","Dropbox","Project assets and shared files","https://www.dropbox.com/home");
        gap(root,10);
        TextView disclaimer=text("KBT Operations v1.0  •  Native Android launcher and workspace dashboard\nThird-party API connections and native Codex task execution are not yet implemented.",12,MUTED,false);disclaimer.setGravity(Gravity.CENTER);root.addView(disclaimer);
    }
}