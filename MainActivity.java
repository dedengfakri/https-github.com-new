package com.example.titannativelite;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    final int bg=Color.rgb(13,19,28),panel=Color.rgb(23,31,43),green=Color.rgb(80,150,105);
    Chess chess=new Chess(); ChessBoard board; TextView status,analysis; int selected=-1;
    TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    GradientDrawable background(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(16);return d;}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(16,16,16,16);root.setBackgroundColor(bg);
        TextView title=label("TITAN  /  NATIVE LITE",20);title.setTypeface(null,1);root.addView(title);
        TextView sub=label("CHESS ANALYSIS  •  ANDROID",11);sub.setTextColor(Color.LTGRAY);sub.setPadding(0,6,0,12);root.addView(sub);
        status=label("● READY  •  Giliran putih",12);status.setPadding(12,12,12,12);status.setBackground(background(panel));root.addView(status);
        board=new ChessBoard();LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=12;bp.bottomMargin=12;root.addView(board,bp);
        LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(14,12,14,12);card.setBackground(background(panel));TextView heading=label("BEST MOVE  /  BASIC ANALYSIS",11);heading.setTextColor(Color.LTGRAY);card.addView(heading);analysis=label("Pilih bidak, lalu petak tujuan",15);analysis.setPadding(0,8,0,0);card.addView(analysis);root.addView(card);
        LinearLayout row=new LinearLayout(this);row.setPadding(0,12,0,0);for(String name:new String[]{"ANALYZE","UNDO","RESET"}){Button button=new Button(this);button.setText(name);button.setTextSize(12);button.setAllCaps(false);button.setTextColor(Color.WHITE);button.setBackground(background(name.equals("ANALYZE")?green:panel));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,54,1);p.setMargins(3,0,3,0);row.addView(button,p);button.setOnClickListener(v->{selected=-1;if(name.equals("RESET")){chess.reset();analysis.setText("Posisi awal");}else if(name.equals("UNDO")){analysis.setText(chess.undo()?"Langkah dikembalikan":"Belum ada langkah");}else{analysis.setText("Menghitung...");new Thread(()->{String result=chess.bestMove();runOnUiThread(()->analysis.setText(result));}).start();}status.setText("● "+chess.result());board.invalidate();});}root.addView(row);setContentView(root);
    }
    class ChessBoard extends View {Paint paint=new Paint(3);String symbols="";ChessBoard(){super(MainActivity.this);}protected void onDraw(Canvas canvas){super.onDraw(canvas);float size=Math.min(getWidth(),getHeight()),cell=size/8f,ox=(getWidth()-size)/2,oy=(getHeight()-size)/2;for(int r=0;r<8;r++)for(int c=0;c<8;c++){int i=r*8+c;paint.setColor((r+c)%2==0?Color.rgb(225,226,206):Color.rgb(95,135,112));canvas.drawRect(ox+c*cell,oy+r*cell,ox+(c+1)*cell,oy+(r+1)*cell,paint);if(i==selected){paint.setColor(0x99FFCE3D);canvas.drawRect(ox+c*cell,oy+r*cell,ox+(c+1)*cell,oy+(r+1)*cell,paint);}char piece=chess.squares[i];String symbol=pieceSymbol(piece);if(!symbol.isEmpty()){paint.setTypeface(Typeface.create("serif",Typeface.NORMAL));paint.setTextSize(cell*.77f);paint.setTextAlign(Paint.Align.CENTER);paint.setColor(Character.isUpperCase(piece)?Color.WHITE:Color.rgb(20,25,29));canvas.drawText(symbol,ox+(c+.5f)*cell,oy+(r+.78f)*cell,paint);}}}
        String pieceSymbol(char p){switch(p){case 'K':return "♔";case 'Q':return "♕";case 'R':return "♖";case 'B':return "♗";case 'N':return "♘";case 'P':return "♙";case 'k':return "♚";case 'q':return "♛";case 'r':return "♜";case 'b':return "♝";case 'n':return "♞";case 'p':return "♟";default:return "";}}
        public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float size=Math.min(getWidth(),getHeight()),cell=size/8f,ox=(getWidth()-size)/2,oy=(getHeight()-size)/2;float x=e.getX()-ox,y=e.getY()-oy;if(x<0||y<0||x>=size||y>=size)return true;int idx=(int)(y/cell)*8+(int)(x/cell);if(selected<0){if(Chess.mine(chess.squares[idx],chess.whiteTurn))selected=idx;}else{if(selected==idx)selected=-1;else if(chess.move(selected,idx)){analysis.setText("Langkah dimainkan");selected=-1;}else if(Chess.mine(chess.squares[idx],chess.whiteTurn))selected=idx;else{analysis.setText("Langkah tidak sah");selected=-1;}}status.setText("● "+chess.result());invalidate();return true;}
    }
}
