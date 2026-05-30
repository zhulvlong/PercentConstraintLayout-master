package com.github.layout;

public class PercentLayoutViewParams {
    public float widthPercent = 0;
    public float heightPercent = 0;
    public float marginLeftPercent = 0;
    public float marginRightPercent = 0;
    public float marginTopPercent = 0;
    public float marginBottomPercent = 0;
    public float paddingLeftPercent = 0;
    public float paddingRightPercent = 0;
    public float paddingTopPercent = 0;
    public float paddingBottomPercent = 0;

    public void initParams(PercentLayoutParamsData data) {
        widthPercent = data.layout_widthPercent;
        heightPercent = data.layout_heightPercent;
        marginTopPercent = data.layout_marginTopPercent;
        marginBottomPercent = data.layout_marginBottomPercent;
        paddingTopPercent = data.layout_paddingTopPercent;
        paddingBottomPercent = data.layout_paddingBottomPercent;
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
        if (data.layout_paddingLeftPercent != 0) {
            paddingLeftPercent = data.layout_paddingLeftPercent;
        } else if (data.layout_paddingStartPercent != 0) {
            paddingLeftPercent = data.layout_paddingStartPercent;
        }
        if (data.layout_paddingRightPercent != 0) {
            paddingRightPercent = data.layout_paddingRightPercent;
        } else if (data.layout_paddingEndPercent != 0) {
            paddingRightPercent = data.layout_paddingEndPercent;
        }
    }
}
