package com.example.titannativelite;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;

public class MainActivity extends Activity {
    int bg=Color.rgb(13,19,28), panel=Color.rgb(23,31,43), green=Color.rgb(80,150,105);
    TextView status, moveText;
    Board board;
    String[] pieces={"♜","♞","♝","♛","♚","♝","♞","♜","♟","♟","♟","♟","♟","♟","♟","♟","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","♙","♙","♙","♙","♙","♙","♙","♙","♖","♘","♗","♕","♔","♗","♘","♖"};
    int selected=-1, moveCount=0;
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg); build();}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(16,14,16,12);root.setBackgroundColor(bg);
        TextView title=text("TITAN  /  NATIVE LITE",20,Color.WHITE,true);root.addView(title);
        TextView subtitle=text("CHESS ANALYSIS  •  ANDROID",11,Color.rgb(145,163,180),true); subtitle.setPadding(0,3,0,16);root.addView(subtitle);
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.setPadding(12,10,12,10);top.setBackground(shape(panel,14));
        TextView badge=text("● READY",12,Color.rgb(130,220,160),true);top.addView(badge,new LinearLayout.LayoutParams(0,-2,1));
        TextView mode=text("LOCAL BOARD",11,Color.LTGRAY,true);top.addView(mode);root.addView(top);
        board=new Board();LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=14;bp.bottomMargin=12;root.addView(board,bp);
        LinearLayout eval=new LinearLayout(this);eval.setOrientation(1);eval.setPadding(14,12,14,12);eval.setBackground(shape(panel,14));
        TextView cap=text("BEST MOVE  /  DEMO",10,Color.rgb(145,163,180),true);eval.addView(cap);
        moveText=text("Tap a piece, then tap a square",15,Color.WHITE,true);moveText.setPadding(0,6,0,0);eval.addView(moveText);
        status=text("Board ready • offline interface",11,Color.LTGRAY,false);status.setPadding(0,7,0,0);eval.addView(status);root.addView(eval);
        LinearLayout buttons=new LinearLayout(this);buttons.setPadding(0,12,0,0);
        String[] names={"ANALYZE","UNDO","RESET"};
        for(String n:names){Button b=new Button(this);b.setText(n);b.setTextSize(11);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setBackground(shape(n.equals("ANALYZE")?green:panel,12));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,52,1);p.setMargins(3,0,3,0);buttons.addView(b,p);
            b.setOnClickListener(v->{if(n.equals("RESET")){reset();}else if(n.equals("UNDO")){selected=-1;status.setText("Undo demo: select another move");board.invalidate();}else{moveText.setText("Engine not connected");status.setText("This clone is a UI starter; Stockfish engine must be added separately.");}});
        }root.addView(buttons);setContentView(root);
    }
    void reset(){String[] start={"♜","♞","♝","♛","♚","♝","♞","♜","♟","♟","♟","♟","♟","♟","♟","♟","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","♙","♙","♙","♙","♙","♙","♙","♙","♖","♘","♗","♕","♔","♗","♘","♖"};pieces=start;selected=-1;moveCount=0;moveText.setText("Tap a piece, then tap a square");status.setText("Board reset");board.invalidate();}
    class Board extends View{
        Paint p=new Paint(3); int light=Color.rgb(224,225,205), dark=Color.rgb(93,133,111);
        Board(){super(MainActivity.this);}
        protected void onDraw(Canvas c){super.onDraw(c);float size=Math.min(getWidth(),getHeight());float cell=size/8f;float ox=(getWidth()-size)/2,oy=(getHeight()-size)/2;
            for(int r=0;r<8;r++)for(int col=0;col<8;col++){int idx=r*8+col; p.setColor(((r+col)%2==0)?light:dark);c.drawRect(ox+col*cell,oy+r*cell,ox+(col+1)*cell,oy+(r+1)*cell,p);
                if(selected==idx){p.setColor(Color.argb(115,255,205,60));c.drawRect(ox+col*cell,oy+r*cell,ox+(col+1)*cell,oy+(r+1)*cell,p);}
                String s=pieces[idx];if(s!=null&&!s.isEmpty()){p.setTextAlign(Paint.Align.CENTER);p.setTextSize(cell*.72f);p.setTypeface(Typeface.create("serif",Typeface.NORMAL));p.setColor((s.equals("♟")||s.equals("♜")||s.equals("♞")||s.equals("♝")||s.equals("♛")||s.equals("♚"))?Color.rgb(25,31,35):Color.WHITE);c.drawText(s,ox+(col+.5f)*cell,oy+(r+.76f)*cell,p);}
            }}
        public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=1)return true;float size=Math.min(getWidth(),getHeight()),cell=size/8f,ox=(getWidth()-size)/2,oy=(getHeight()-size)/2;int col=(int)((e.getX()-ox)/cell),r=(int)((e.getY()-oy)/cell);if(r<0||r>7||col<0||col>7)return true;int idx=r*8+col;
            if(selected<0){selected=idx;status.setText("Square selected: "+(char)('a'+col)+(8-r));}
            else if(selected==idx){selected=-1;status.setText("Selection cleared");}
            else {pieces[idx]=pieces[selected];pieces[selected]="";selected=-1;moveCount++;moveText.setText("Move "+moveCount+" • board updated");status.setText("Demo move only — legality is not checked");}
            invalidate();return true;}
    }
}