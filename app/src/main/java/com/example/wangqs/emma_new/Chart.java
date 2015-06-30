package com.example.wangqs.emma_new;

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
/**
 * Created by wangqs on 7/16/14.
 */
public class Chart {
    public GraphicalView hist;
    private XYMultipleSeriesDataset dataset;
    private Context chart_context;
    private XYSeries series;
    private XYMultipleSeriesRenderer renderer;
    //    private int l;
    private int max;
//  private int[] X_num;
//  private int[] Y_num;

    public GraphicalView draw(int[] bar, Context c){
        chart_context = c;
        series = new XYSeries("histogram");
        int l = bar.length;
        max=0;
        for(int i=0; i<l; i++){
            series.add(i,bar[i]);
            if(max<bar[i])
                max = bar[i];
//          X_num[i] = i+1;
        }
        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        int[] colors = new int[] {Color.parseColor("#436EEE")}; //DCE2F1
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
        renderer = buildRenderer(colors, styles, true);
        renderer.setMargins(new int[] {3, 3, -3, 3});


        setChartSettings(renderer, 0, max, 0, l);
        hist = ChartFactory.getBarChartView(chart_context,dataset,renderer,null);
        return hist;
    }

    public GraphicalView update(int[] bar){

        dataset.removeSeries(series);
        series.clear();
/*        for(SimpleSeriesRenderer M_renderer:renderer.getSeriesRenderers()){
            renderer.removeSeriesRenderer(M_renderer);
        }
*/
        int l = bar.length;
        max=0;
        for(int i=0; i<l; i++){
            series.add(i,bar[i]);
            if(max<bar[i])
                max = bar[i];
//          X_num[i] = i+1;
        }
        dataset.addSeries(series);
        setChartSettings(renderer, 0, max, 0, l);
        hist.invalidate();

        return hist;
    }

    private XYMultipleSeriesRenderer buildRenderer(int[] colors,PointStyle[] styles, boolean b) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            r.setChartValuesSpacing((float) 0.5);
            //            r.setPointStyle(styles[i]);
            //            r.setFillPoints(b);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    private void setChartSettings(XYMultipleSeriesRenderer renderer, int yMin, double yMax, int xMin, int xMax) {

//                 renderer.setChartTitle(title);
//                 renderer.setXTitle(xTitle);
//                 renderer.setYTitle(yTitle);
        double Y_Variable_Max;
        renderer.setYAxisMin(yMin);
        if(yMax>10)
            Y_Variable_Max = 1.2*yMax;
        else
            Y_Variable_Max = yMax+1;
        renderer.setYAxisMax(Y_Variable_Max);
//                 renderer.setAxesColor(axesColor);
//                 renderer.setLabelsColor(labelsColor);
//                 renderer.setXLabels(7);
        renderer.setYLabels(0);
//                 renderer.setXLabelsColor(Color.BLACK);//X轴坐标颜色
//                 renderer.setYLabelsColor(0, Color.BLACK);
//                 renderer.setYLabelsAlign(Align.LEFT);
        //renderer.setPanEnabled(false, false);//设置xy轴不会因用户划动屏幕而移动
//                 renderer.setMarginsColor(Color.WHITE);//设置四周背景色
//                 renderer.setBackgroundColor(Color.WHITE);//设置背景
//                 renderer.setApplyBackgroundColor(true);
//                 renderer.setDisplayChartValues(true);//设置顶部显示数据
//                 renderer.setZoomButtonsVisible(true);//设置可以缩放
        renderer.setBarSpacing(0.2);//设置柱子间距
        renderer.setFitLegend(true);// 调整合适的位置
        renderer.setXAxisMin(xMin-0.5);
        renderer.setXAxisMax(xMax-0.5);
        renderer.setXLabels(0);//设置X轴不显示数字
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.setShowLegend(false);
        renderer.setLegendHeight(0);

//                 for(int i=1;i<13;i++){
//                        renderer.addXTextLabel((double)i, days[i-1]);
//                 }
    }
}