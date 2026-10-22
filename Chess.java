package com.example.titannativelite;

import java.util.*;

/** Small self-contained chess rules implementation. Uppercase = white. */
public final class Chess {
    public char[] squares = new char[64];
    public boolean whiteTurn = true;
    public boolean wk=true,wq=true,bk=true,bq=true;
    public int enPassant=-1;
    private final Stack<State> history = new Stack<>();
    public Chess(){reset();}
    public void reset(){String start="rnbqkbnrpppppppp................................PPPPPPPPRNBQKBNR";squares=start.toCharArray();whiteTurn=true;wk=wq=bk=bq=true;enPassant=-1;history.clear();}
    static boolean white(char p){return p>='A'&&p<='Z';}
    static boolean black(char p){return p>='a'&&p<='z';}
    static boolean mine(char p,boolean w){return p!='.'&&(w?white(p):black(p));}
    static boolean enemy(char p,boolean w){return p!='.'&&(w?black(p):white(p));}
    static int row(int i){return i/8;} static int col(int i){return i%8;}
    static boolean inside(int r,int c){return r>=0&&r<8&&c>=0&&c<8;}
    static int sq(int r,int c){return r*8+c;}
    public static final class Move {public final int from,to; public final char promotion; Move(int f,int t,char p){from=f;to=t;promotion=p;} public String toString(){return ""+(char)('a'+col(from))+(8-row(from))+(char)('a'+col(to))+(8-row(to))+(promotion=='.'?"":Character.toLowerCase(promotion));}}
    private static final class State {char[] board;boolean turn,wk,wq,bk,bq;int ep;State(Chess c){board=c.squares.clone();turn=c.whiteTurn;wk=c.wk;wq=c.wq;bk=c.bk;bq=c.bq;ep=c.enPassant;}}
    private void restore(State s){squares=s.board;whiteTurn=s.turn;wk=s.wk;wq=s.wq;bk=s.bk;bq=s.bq;enPassant=s.ep;}
    private void add(List<Move> out,int from,int r,int c){if(inside(r,c)){char p=squares[from];char dst=squares[sq(r,c)];if(!mine(dst,white(p))&&Character.toLowerCase(dst)!='k'){char promo=Character.toLowerCase(p)=='p'&&(r==0||r==7)?(white(p)?'Q':'q'):'.';out.add(new Move(from,sq(r,c),promo));}}}
    private List<Move> pseudo(boolean side){List<Move> out=new ArrayList<>();for(int i=0;i<64;i++){char p=squares[i];if(!mine(p,side))continue;int r=row(i),c=col(i);switch(Character.toLowerCase(p)){
        case 'p': {int d=side?-1:1,nr=r+d;if(inside(nr,c)&&squares[sq(nr,c)]=='.'){add(out,i,nr,c);int initial=side?6:1;if(r==initial&&squares[sq(r+2*d,c)]=='.')add(out,i,r+2*d,c);}for(int dc:new int[]{-1,1}){int nc=c+dc;if(inside(nr,nc)&&(enemy(squares[sq(nr,nc)],side)||sq(nr,nc)==enPassant))add(out,i,nr,nc);}break;}
        case 'n': {int[][] ds={{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};for(int[] d:ds)add(out,i,r+d[0],c+d[1]);break;}
        case 'k': {for(int dr=-1;dr<=1;dr++)for(int dc=-1;dc<=1;dc++)if(dr!=0||dc!=0)add(out,i,r+dr,c+dc);
            if(side&&i==60&&!inCheck(true)){if(wk&&squares[61]=='.'&&squares[62]=='.'&&squares[63]=='R'&&!attacked(61,false)&&!attacked(62,false))out.add(new Move(60,62,'.'));if(wq&&squares[59]=='.'&&squares[58]=='.'&&squares[57]=='.'&&squares[56]=='R'&&!attacked(59,false)&&!attacked(58,false))out.add(new Move(60,58,'.'));}
            if(!side&&i==4&&!inCheck(false)){if(bk&&squares[5]=='.'&&squares[6]=='.'&&squares[7]=='r'&&!attacked(5,true)&&!attacked(6,true))out.add(new Move(4,6,'.'));if(bq&&squares[3]=='.'&&squares[2]=='.'&&squares[1]=='.'&&squares[0]=='r'&&!attacked(3,true)&&!attacked(2,true))out.add(new Move(4,2,'.'));}break;}
        default: {int[][] dirs={{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};for(int j=0;j<8;j++){if(Character.toLowerCase(p)=='b'&&j<4)continue;if(Character.toLowerCase(p)=='r'&&j>=4)continue;int nr=r+dirs[j][0],nc=c+dirs[j][1];while(inside(nr,nc)){if(mine(squares[sq(nr,nc)],side))break;add(out,i,nr,nc);if(squares[sq(nr,nc)]!='.')break;nr+=dirs[j][0];nc+=dirs[j][1];}}}
    }}return out;}
    private boolean attacked(int target,boolean byWhite){int tr=row(target),tc=col(target);for(int i=0;i<64;i++){char p=squares[i];if(!mine(p,byWhite))continue;int r=row(i),c=col(i),dr=tr-r,dc=tc-c;switch(Character.toLowerCase(p)){
        case 'p':if(dr==(byWhite?-1:1)&&Math.abs(dc)==1)return true;break;
        case 'n':if(Math.abs(dr)*Math.abs(dc)==2)return true;break;
        case 'k':if(Math.max(Math.abs(dr),Math.abs(dc))==1)return true;break;
        default:boolean straight=(dr==0||dc==0),diag=Math.abs(dr)==Math.abs(dc);char kind=Character.toLowerCase(p);if(!(kind=='q'&&(straight||diag)||kind=='r'&&straight||kind=='b'&&diag))break;int sr=Integer.signum(dr),sc=Integer.signum(dc),rr=r+sr,cc=c+sc;boolean clear=true;while(rr!=tr||cc!=tc){if(squares[sq(rr,cc)]!='.'){clear=false;break;}rr+=sr;cc+=sc;}if(clear)return true;
    }}return false;}
    public boolean inCheck(boolean side){char king=side?'K':'k';for(int i=0;i<64;i++)if(squares[i]==king)return attacked(i,!side);return true;}
    private void apply(Move m){char p=squares[m.from],captured=squares[m.to];boolean side=white(p);if(p=='K'){wk=wq=false;}if(p=='k'){bk=bq=false;}if(m.from==63||m.to==63)wk=false;if(m.from==56||m.to==56)wq=false;if(m.from==7||m.to==7)bk=false;if(m.from==0||m.to==0)bq=false;
        if(Character.toLowerCase(p)=='p'&&m.to==enPassant&&captured=='.')squares[m.to+(side?8:-8)]='.';
        squares[m.to]=m.promotion=='.'?p:m.promotion;squares[m.from]='.';
        if(Character.toLowerCase(p)=='k'&&Math.abs(m.to-m.from)==2){if(m.to==62){squares[61]='R';squares[63]='.';}if(m.to==58){squares[59]='R';squares[56]='.';}if(m.to==6){squares[5]='r';squares[7]='.';}if(m.to==2){squares[3]='r';squares[0]='.';}}
        enPassant=Character.toLowerCase(p)=='p'&&Math.abs(m.to-m.from)==16?(m.from+m.to)/2:-1;whiteTurn=!whiteTurn;
    }
    public List<Move> legalMoves(){List<Move> valid=new ArrayList<>();for(Move m:pseudo(whiteTurn)){State s=new State(this);apply(m);if(!inCheck(s.turn))valid.add(m);restore(s);}return valid;}
    public boolean move(int from,int to){for(Move m:legalMoves())if(m.from==from&&m.to==to){history.push(new State(this));apply(m);return true;}return false;}
    public boolean undo(){if(history.empty())return false;restore(history.pop());return true;}
    public String result(){List<Move> moves=legalMoves();if(!moves.isEmpty())return inCheck(whiteTurn)?"Skak":"Giliran "+(whiteTurn?"putih":"hitam");return inCheck(whiteTurn)?"Skakmat — "+(whiteTurn?"hitam":"putih")+" menang":"Remis — stalemate";}
    public int evaluate(){int total=0;for(char p:squares){int v=0;switch(Character.toLowerCase(p)){case 'p':v=100;break;case 'n':v=320;break;case 'b':v=330;break;case 'r':v=500;break;case 'q':v=900;break;}total+=white(p)?v:-v;}return total;}
    public String bestMove(){List<Move> moves=legalMoves();if(moves.isEmpty())return "Tidak ada langkah legal";Move best=null;int bestScore=whiteTurn?Integer.MIN_VALUE:Integer.MAX_VALUE;for(Move m:moves){State s=new State(this);apply(m);int score=evaluate();List<Move> replies=legalMoves();if(replies.isEmpty()&&inCheck(whiteTurn))score=whiteTurn?100000:-100000;else if(!replies.isEmpty()){int worst=whiteTurn?Integer.MIN_VALUE:Integer.MAX_VALUE;for(Move reply:replies){State s2=new State(this);apply(reply);int e=evaluate();worst=whiteTurn?Math.max(worst,e):Math.min(worst,e);restore(s2);}score=worst;}restore(s);if(best==null||(whiteTurn?score>bestScore:score<bestScore)){best=m;bestScore=score;}}return best.toString()+"  |  evaluasi material: "+String.format(java.util.Locale.US,"%+.2f",bestScore/100.0)+" (bukan Stockfish)";}
}
