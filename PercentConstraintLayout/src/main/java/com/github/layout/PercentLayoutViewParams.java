package com.github.layout;

public class PercentLayoutViewParams {
    public float widthPercent = 0;
    public float heightPercent = 0;
    public float marginLeftPercent = 0;
    public float marginRightPercent = 0;
    public float marginTopPercent = 0;
    public float marginBottomPercent = 0;

    public void initParams( PercentLayoutParamsData data) {
        widthPercent = data.layout_widthPercent;
        heightPercent = data.layout_heightPercent;
        marginTopPercent = data.layout_marginTopPercent;
        marginBottomPercent = data.layout_marginBottomPercent;
        if (data.layout_marginLeftPercent != 0) {
            marginLeftPercent = data.layout_marginLeftPercent;
        } else if (data.layout_marginStartPercent != 0) {
            marginLeftPercent = data.layout_marginStartPercent;
        }
        if (data.layout_marginRightPercent != 0) {
            marginRightPercent = data.layout_marginRightPercent;
        } else if (data.layout_marginEndPercent != 0) {
            marginRightPercent = data.layout_marginEndPercent;
        }
    }
}
