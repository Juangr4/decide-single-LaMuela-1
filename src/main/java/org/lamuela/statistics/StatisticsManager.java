package org.lamuela.statistics;
import java.util.List;

import org.lamuela.api.models.Option;
import org.lamuela.api.models.Question;
import org.lamuela.api.models.Voting;

public class StatisticsManager {

    public static String getGraphOfVotingId(int id, ChartType type) throws Exception{
        //Voting voting = DecideAPI.getVotingById(id);
        Voting voting = getExampleVoting();
        switch (type) {
            case BAR:
                return getBarGraph(voting);
            case PIE:
                return getPieGraph(voting);
            case HORIZONTALBAR:
                return getHorizontalBarGraph(voting);
            case DOUGHNUT:
                return getDoughnutGraph(voting);
            case POLAR:
                return getPolarGraph(voting);
            default:
                throw new Exception();
        }
    }


    private static String getPolarGraph(Voting voting) {
        String options = "";
        String datasets = "";
        for (Option option : voting.getQuestion().getOptions()){
            options += String.format("'%s',", option.getOption());
            datasets += String.format("'%d',", option.getNumber());
        }
        options = options.substring(0, options.length()-1);
        datasets = datasets.substring(0, datasets.length()-1);
        return String.format("https://quickchart.io/chart?c={type:'polarArea',data:{labels:[%s], datasets:[{data:[%s]}]}}", options, datasets);
    }


    private static String getHorizontalBarGraph(Voting voting) {
        String datasets = "";
        for(Option o : voting.getQuestion().getOptions()){
            String dataset = String.format("{label:'%s',data:[%d]},", o.getOption(), o.getNumber());
            datasets+=dataset;
        }
        datasets = datasets.substring(0, datasets.length()-1);

        return String.format("https://quickchart.io/chart?c={type:'horizontalBar',data:{labels:['%s'], datasets:[%s]}}", voting.getQuestion().getDesc(), datasets);
    }

    private static String getDoughnutGraph(Voting voting) {
        String options = "";
        String datasets = "";
        for (Option option : voting.getQuestion().getOptions()){
            options += String.format("'%s',", option.getOption());
            datasets += String.format("'%d',", option.getNumber());
        }
        options = options.substring(0, options.length()-1);
        datasets = datasets.substring(0, datasets.length()-1);
        Integer total = voting.getQuestion().getOptions().stream().map(o -> o.getNumber()).reduce(0, Integer::sum);
        return String.format("https://quickchart.io/chart?c={type:'doughnut',data:{labels:[%s], datasets:[{data:[%s]}]},options:{plugins:{doughnutlabel:{labels:[{text:'%d',font:{size:20}},{text:'total'}]}}}}", options, datasets, total);
    }


    private static String getPieGraph(Voting voting) {

        String options = "";
        String datasets = "";
        for (Option option : voting.getQuestion().getOptions()){
            options += String.format("'%s',", option.getOption());
            datasets += String.format("'%d',", option.getNumber());
        }
        options = options.substring(0, options.length()-1);
        datasets = datasets.substring(0, datasets.length()-1);
        return String.format("https://quickchart.io/chart?c={type:'pie',data:{labels:[%s], datasets:[{data:[%s]}]}}", options, datasets);
    }


    private static String getBarGraph(Voting voting) {
        String datasets = "";
        for(Option o : voting.getQuestion().getOptions()){
            String dataset = String.format("{label:'%s',data:[%d]},", o.getOption(), o.getNumber());
            datasets+=dataset;
        }
        datasets = datasets.substring(0, datasets.length()-1);

        return String.format("https://quickchart.io/chart?c={type:'%s',data:{labels:['%s'], datasets:[%s]}}", ChartType.BAR.toString().toLowerCase(), voting.getQuestion().getDesc(), datasets);
    }


    public static Voting getExampleVoting(){
        Voting voting = new Voting();
        Question q = new Question();
        q.setDesc("Descripción de prueba");
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        o1.setNumber(1);
        o1.setOption("Option 1");
        o2.setNumber(2);
        o2.setOption("Option 2");
        o3.setNumber(3);
        o3.setOption("Option 3");

        List<Option> options = List.of(o1, o2, o3);
        q.setOptions(options);
        voting.setQuestion(q);
        return voting;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(getGraphOfVotingId(1,ChartType.BAR));
    }
    
}
