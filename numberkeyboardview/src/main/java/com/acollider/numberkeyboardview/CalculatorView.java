package com.acollider.numberkeyboardview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by s.parkhomenko on 16.07.2015.
 */
public class CalculatorView extends FrameLayout{
    private String separator = ",";
    private String[] values = new String[]{"1","2","3","4","5","6","7","8","9",separator,"0","Backsp"};
    private Context context;
    private TextView tvDisplay;
    private String integers = "";
    private String hundredths = "";
    private boolean workingWithIntegers = true;
    private int displayTextSize = 38;
    private int buttonsTextSize = 26;
    private int numberLengthLimit = 9;
    private boolean showSpaces = false;
    private boolean showSelectors = false;

    public CalculatorView(Context context) {
        super(context);
        this.context = context;

    }

    public void build(){
        initKeyboard();
    }

    private void initKeyboard(){
        View mainView =((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.calculator_layout, this);
        tvDisplay = (TextView)mainView.findViewById(R.id.display);
        tvDisplay.setTextSize(displayTextSize);

        LinearLayout keyBoardView1Row = (LinearLayout)mainView.findViewById(R.id.keyboard_1row);
        LinearLayout keyBoardView2Row = (LinearLayout)mainView.findViewById(R.id.keyboard_2row);
        LinearLayout keyBoardView3Row = (LinearLayout)mainView.findViewById(R.id.keyboard_3row);
        LinearLayout keyBoardView4Row = (LinearLayout)mainView.findViewById(R.id.keyboard_4row);
        LinearLayout[] rows = new LinearLayout[]{keyBoardView1Row,keyBoardView2Row,keyBoardView3Row,keyBoardView4Row};

        int rowIndex = 0;
        int j = 0;
        for (int i=0; i<values.length-1; i++){
            TextView btn = new TextView(context);
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight=(float)0.33;
            btn.setTextSize(buttonsTextSize);
            btn.setLayoutParams(params);
            btn.setGravity(Gravity.CENTER);
            btn.setText(values[i]);
            btn.setTag(values[i]);
            if (showSelectors){
                btn.setBackgroundResource(R.drawable.bg_selector);
            }
            btn.setOnClickListener(clickListener);
            rows[rowIndex].addView(btn);
            j++;
            if (j==3){
                rowIndex++;
                j=0;
            }
        }

        FrameLayout fl = new FrameLayout(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight=(float)0.33;
        fl.setLayoutParams(params);
        if (showSelectors){
            fl.setBackgroundResource(R.drawable.bg_selector);
        }
        fl.setTag(values[11]);
        fl.setOnClickListener(clickListener);
        keyBoardView4Row.addView(fl);

        ImageView baksp = new ImageView(context);
        params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        baksp.setLayoutParams(params);
        baksp.setImageResource(R.drawable.ic_action_backspace);
        fl.addView(baksp);
    }

    private View.OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String input = (String)v.getTag();
            performInput(input);
        }
    };

    private void performInput(String input){
        if (input.equals(separator)){
            workingWithIntegers = false;
        } else if (input.equals("Backsp")){
            performBacksp();
        } else {
            addNumber(input);
        }
    }

    private void addNumber(String input){
        StringBuilder inputSB;
        if (workingWithIntegers){
            inputSB = new StringBuilder(integers);
            if (integers.length()==0&&input.equals("0") || integers.length()== numberLengthLimit){
                input = "";
            }
            inputSB.append(input);
            integers = new String(inputSB);
        } else {
            inputSB = new StringBuilder(hundredths);
            if (hundredths.length()==2){
                input = "";
            }
            inputSB.append(input);
            hundredths = new String(inputSB);
        }

        formatAndShow();
    }

    private void performBacksp(){
        StringBuilder inputSB;
        if (workingWithIntegers){
            if (integers.length()>0){
                inputSB = new StringBuilder(integers);
                inputSB.delete(inputSB.length() - 1, inputSB.length());
                integers = new String(inputSB);
            }
        } else {
            inputSB = new StringBuilder(hundredths);
            if (hundredths.length()==0){
                workingWithIntegers = true;
            } else {
                inputSB.delete(inputSB.length() - 1, inputSB.length());
                hundredths = new String(inputSB);
            }
        }
        formatAndShow();
    }

    public void formatAndShow(){
        String formatedIntegers = formatIntegers();
        String formatedHundredths = formatHundredths();
        String textToShow = formatedIntegers + separator + formatedHundredths;
        if (showSpaces){
            textToShow = addSpaces(textToShow);
        }
        tvDisplay.setText(textToShow);
    }

    private String formatIntegers(){
        if (integers.length()==0){
            return "0";
        }
        return integers;
    }

    private String formatHundredths(){
        if (hundredths.length()==0){
            return "00";
        } else if(hundredths.length()==1){
            return hundredths+"0";
        }
        return hundredths;
    }

    private String addSpaces(String text){
        String input = text.replaceAll("\\s+", "");
        int j = input.length();

        if (j > 3) {

            String res;
            if (input.contains(separator)) {
                j = input.indexOf(separator);
                StringBuilder sb = new StringBuilder(input.substring(0, j));
                String append = input.substring(j, input.length());
                for (int k = sb.length() - 3; k > 0; k -= 3) {
                    sb.insert(k, " ");
                }
                res = sb.toString() + append;
            } else {
                StringBuilder sb = new StringBuilder(input);
                for (int k = sb.length() - 3; k > 0; k -= 3) {
                    sb.insert(k, " ");
                }
                res = sb.toString();
            }
            return res;
        }
        return input;
    }

    public int getNumberLengthLimit() {
        return numberLengthLimit;
    }

    public void setNumberLengthLimit(int numberLengthLimit) {
        this.numberLengthLimit = numberLengthLimit;
    }

    public int getButtonsTextSize() {
        return buttonsTextSize;
    }

    public void setButtonsTextSize(int buttonsTextSize) {
        this.buttonsTextSize = buttonsTextSize;
    }

    public int getDisplayTextSize() {
        return displayTextSize;
    }

    public void setDisplayTextSize(int displayTextSize) {
        this.displayTextSize = displayTextSize;
    }

    public boolean isShowSpaces() {
        return showSpaces;
    }

    public void setShowSpaces(boolean showSpaces) {
        this.showSpaces = showSpaces;
    }

    public String getCalculatorValue(){
        return tvDisplay.getText().toString();
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean isShowSelectors() {
        return showSelectors;
    }

    public void setShowSelectors(boolean showSelectors) {
        this.showSelectors = showSelectors;
    }

    public void setIntegers(String integers) {
        this.integers = integers;
    }

    public void setHundredths(String hundredths) {
        this.hundredths = hundredths;
    }

}
