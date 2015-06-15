import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

/**
 * @author Marko Bastovanovic (markob@vast.com)
 */

public class CrawlCharts {
    public static final String CHART_HOME = "http://www.officialcharts.com/charts/dance-singles-chart/";
    // next to process is je http://www.officialcharts.com/charts/dance-singles-chart/20091031/104
    public static final String STORE_LOC = "/tmp/official_charts";

    public static void main (String [] args){
        try{
            if (new File(STORE_LOC).exists()){
                System.out.println(STORE_LOC + " already exists. Quitting");
                return;
            }else{
                if (!new File(STORE_LOC).mkdirs()){
                    System.out.println("Can't create dir " + STORE_LOC);
                }
            }

            String nextUrl = getNextChartIdAndDownloadThisOne(CHART_HOME);
            while (nextUrl != null){
                System.out.println("Processing " + nextUrl);
                nextUrl = getNextChartIdAndDownloadThisOne(nextUrl);
            }

            System.out.println("Done");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String getNextChartIdAndDownloadThisOne(String url) throws Exception{
        Document document = Jsoup.connect(url).get();

        String thisChartID = document.body().getElementById("this-chart-id").val();
        if (thisChartID == null){
            throw new RuntimeException("Can't determine this-chart-id for \n\n" + document);
        }
        FileUtils.writeStringToFile(new File(STORE_LOC + "/" + thisChartID), document.toString());

        for (Element element : document.body().getAllElements()) {
            if (element.className().equals("prev chart-date-directions")){
                return element.attr("abs:href");
            }
        }
        return null;
    }
}
