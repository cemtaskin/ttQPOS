package ttqrpos.com.ttposmobile.HuginTechPos;

public class FreePrintAttr {
    public String align;
    public String font;
    public boolean linefeed;
    public String style;
    public int offset;
    public int height;
    public int width;

    public FreePrintAttr(String align,String font,boolean linefeed,String style,int offset,int height,int width){
        this.align=align;
        this.font=font;
        this.linefeed=linefeed;
        this.style=style;
        this.offset=offset;
        this.height=height;
        this.width=width;
    }

}