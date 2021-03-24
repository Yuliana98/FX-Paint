package model;

import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "text")
public class Text extends Shape {

    private Dot dotText;
    private String text;
    private Font textFont;
    private double lineSpacing;
    private boolean strikeThrough;
    private TextAlignment textAlignment;
    private VPos textOrigin;
    private boolean underLine;

    public Text(int w, String fillColor, String strokeColor, Dot dotText, String text, Font textFont, double lineSpacing,
                boolean strikeThrough, TextAlignment textAlignment, VPos textOrigin, boolean underLine) {
        super(w, fillColor, strokeColor);
        this.dotText = dotText;
        this.text = text;
        this.textFont = textFont;
        this.lineSpacing = lineSpacing;
        this.strikeThrough = strikeThrough;
        this.textAlignment = textAlignment;
        this.textOrigin = textOrigin;
        this.underLine = underLine;
    }

    public Text(Dot dotText, String text, Font textFont, double lineSpacing, boolean strikeThrough,
                TextAlignment textAlignment, VPos textOrigin, boolean underLine) {
        this.dotText = dotText;
        this.text = text;
        this.textFont = textFont;
        this.lineSpacing = lineSpacing;
        this.strikeThrough = strikeThrough;
        this.textAlignment = textAlignment;
        this.textOrigin = textOrigin;
        this.underLine = underLine;
    }

    public Text() {
    }

    public Dot getDotText() {
        return dotText;
    }

    public Text setDotText(Dot dotText) {
        this.dotText = dotText;
        return this;
    }

    public String getText() {
        return text;
    }

    public Text setText(String text) {
        this.text = text;
        return this;
    }

    public Font getTextFont() {
        return textFont;
    }

    public Text setTextFont(Font textFont) {
        this.textFont = textFont;
        return this;
    }

    public double getLineSpacing() {
        return lineSpacing;
    }

    public Text setLineSpacing(double lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    public boolean isStrikeThrough() {
        return strikeThrough;
    }

    public Text setStrikeThrough(boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public Text setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public VPos getTextOrigin() {
        return textOrigin;
    }

    public Text setTextOrigin(VPos textOrigin) {
        this.textOrigin = textOrigin;
        return this;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    public Text setUnderLine(boolean underLine) {
        this.underLine = underLine;
        return this;
    }
}
