import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Marko Bastovanovic (markob@vast.com)
 */

public class ParseStoredHtmlFiles {
    public static void main (String [] args){
        try{
            Multiset<String> points = HashMultiset.create();

            //noinspection ConstantConditions
            for (File file: new File(CrawlCharts.STORE_LOC).listFiles()) {
                if (!(file.toString().contains("-2015") || file.toString().contains("-2014"))){
                    continue;
                }
                System.out.println(file);
                Document document = Jsoup.parse(FileUtils.readFileToString(file));
                Elements chartPositionsTable = document.body()
                        .getElementById("container")
                        .getElementById("main")
                        .getElementsByAttributeValue("class", "page").first()
                        .getElementsByAttributeValue("class", "grid").first()
                        .getElementsByAttributeValue("class", "grid__cell unit-2-3--desktop").first()
                        .getElementsByAttributeValue("class", "chart").first()
                        .getElementsByAttributeValue("class", "chart-positions").first().children();

                for (Element chartPositionsElement: chartPositionsTable.select("tr")) {
                    Element bla = chartPositionsElement.getElementsByClass("position").first();
                    if (bla == null){
                        continue;
                    }
                    points.add(chartPositionsElement.getElementsByClass("artist").first().text()
                                    + " - " + chartPositionsElement.getElementsByClass("title").first().text(),
                            40 - Integer.parseInt(bla.text()));
                }
            }

            TreeMap<Integer, String> sorted = new TreeMap<>();
            for (Multiset.Entry<String> song : points.entrySet()) {
                sorted.put(song.getCount(), song.getElement());
            }

            for (Map.Entry<Integer, String> integerStringEntry : sorted.entrySet()) {
                System.out.println(integerStringEntry.getKey() + " " + integerStringEntry.getValue());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
