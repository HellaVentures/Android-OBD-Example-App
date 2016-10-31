package veronika.hella.obdapp.obd;

import com.github.pires.obd.commands.control.TroubleCodesCommand;

/**
 * Created by Veronika on 13.09.2016.
 */
 class MyTroubleCodesCommand extends TroubleCodesCommand {

    MyTroubleCodesCommand(){
        super();
    }

    /**
     * Method: by Pires, Location changed
     * Removes unwanted response from output since this results in erroneous error codes
     * @return the cleaned raw data
     */
    @Override
    public String getResult() {
        return rawData.replace("SEARCHING...", "").replace("NODATA", "");
    }

    /**
     * Edited CAN transformation from raw data to defined error code to fit to our setup
     */
    @Override
    protected void performCalculations(){
        final String result = getResult();
        String workingData;
        int startIndex = 0;//Header size.

        String canOneFrame = result.replaceAll("[\r\n]", "");
        int canOneFrameLength = canOneFrame.length();
        if (canOneFrameLength <= 16 && canOneFrameLength % 4 == 0) {//CAN(ISO-15765) protocol one frame.
            workingData = canOneFrame;//43yy{codes}
            startIndex = 4;//Header is 43yy, yy showing the number of data items.
        } else if (result.contains(":")) {//CAN(ISO-15765) protocol two and more frames.
            workingData = result.replaceAll("[\r\n].:", "");//Edit: x:43yy{codes}
            startIndex = 4;//Edit: Header is x:43yy, x: showing the frame number beginning at 0, yy showing the number of data items.
        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replaceAll("^43|[\r\n]43|[\r\n]", "");
        }
        for (int begin = startIndex; begin < workingData.length(); begin += 4) {
            String dtc = "";
            byte b1 = (byte) ((Character.digit(workingData.charAt(begin), 16) << 4));
            int ch1 = ((b1 & 0xC0) >> 6);
            int ch2 = ((b1 & 0x30) >> 4);
            dtc += dtcLetters[ch1];
            dtc += hexArray[ch2];
            dtc += workingData.substring(begin+1, begin + 4);
            if (dtc.equals("P0000")) {
                return;
            }
            codes.append(dtc);
            codes.append('\n');
        }
    }
}