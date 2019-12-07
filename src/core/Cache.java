package core;

import com.sun.istack.internal.NotNull;
import tool.Tool;

import java.io.*;
import java.util.Vector;

public class Cache {
    private File currentStorage = new File("./cache/current").getAbsoluteFile();
    private File historyStorage = new File("./cache/history").getAbsoluteFile();
    private File peopleStorage = new File("./cache/people").getAbsoluteFile();

    public boolean isLoaded = false;
    public String title = "";
    public int firstCount = -1;
    public int secondCount = -1;
    public int thirdCount = -1;
    public Vector<History> data = new Vector<>();
    public HistoryTableModel historyTableModel;
    public PeopleTableModel peopleTableModel;
    public ResultTableModel resultTableModel;


    public Cache(){
        historyTableModel = new HistoryTableModel(this);
        peopleTableModel = new PeopleTableModel();
        resultTableModel = new ResultTableModel(new History());
    }

    public void push(@NotNull History history){
        data.add(history);
        int rowIndex = data.size() - 1;
        historyTableModel.fireTableRowsInserted(rowIndex, rowIndex);
    }

    public boolean canExecute(){
        if(title.isEmpty()){
            return false;
        }  if(firstCount < 0 || secondCount < 0 || thirdCount < 0){
            return false;
        }  if(firstCount+secondCount+thirdCount == 0){
            return false;
        }  if(firstCount + secondCount + thirdCount > peopleTableModel.available().size()){
            return false;
        }

        return true;
    }

    public String error(){
        if(title.isEmpty()){
            return "标题不能为空";
        }  if(firstCount < 0 || secondCount < 0 || thirdCount < 0){
            return "请设置奖项";
        }  if(firstCount+secondCount+thirdCount == 0){
            return "奖项数需大于0";
        }  if(firstCount + secondCount + thirdCount > peopleTableModel.available().size()){
            return "奖项数超过人数";
        }

        return null;
    }

    public History getNew(){
        History history = new History();
        history.title = title;
        Vector<String[]> people = peopleTableModel.available();

        String[] prizes = {"一等奖","二等奖","三等奖"};
        for (int i = 0 ;i<prizes.length;i++){
            Vector<String[]> current;
            if(i == 0){
                current = Tool.random(people, firstCount);
            } else if(i == 1){
                current = Tool.random(people,secondCount);
            } else {
                current = Tool.random(people,thirdCount);
            }

            for (String[] element: current) {
                String[] insert = new String[]{prizes[i], element[0], element[1]};
                history.data.add(insert);
            }
        }

        return history;
    }

    public void saveNormalStorage(){
        if (isLoaded){
            peopleTableModel.exportToFile(peopleStorage);
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(currentStorage))) {
                writer.write(title);
                writer.newLine();
                writer.write(String.valueOf(firstCount));
                writer.newLine();
                writer.write(String.valueOf(secondCount));
                writer.newLine();
                writer.write(String.valueOf(thirdCount));
                writer.newLine();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void readStorage(){
        if(!isLoaded){
            isLoaded = true;
            readNormalStorage();
            readHistoryStorage();
        }
    }

    public void readNormalStorage(){
        peopleTableModel.importFromFile(peopleStorage);
        String title = "";
        int firstCount = -1;
        int secondCount = -1;
        int thirdCount = -1;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(currentStorage));
            title = reader.readLine();
            firstCount = Integer.valueOf(reader.readLine());
            secondCount = Integer.valueOf(reader.readLine());
            thirdCount = Integer.valueOf(reader.readLine());

            this.title = title;
            this.firstCount = firstCount;
            this.secondCount = secondCount;
            this.thirdCount = thirdCount;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveHistoryStorage(){
        if(isLoaded){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(historyStorage))){
                for (History history:data) {
                    writer.write("#");
                    writer.write(history.title);
                    writer.newLine();
                    for (String[] element:history.data){
                        writer.write(String.format("%s;%s;%s",element[0],element[1],element[2]));
                        writer.newLine();
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void readHistoryStorage(){
        try(BufferedReader reader = new BufferedReader(new FileReader(historyStorage))){
            Vector<History> data= new Vector<>();
            History history = null;
            String line = "";
            do {
                line = reader.readLine();
                if(line == null || line.isEmpty()){
                    break;
                }
                if(line.startsWith("#")){
                    if(history != null){
                        data.add(history);
                    }
                    history = new History();
                    history.title = line.substring(1);
                } else {
                    assert history != null;
                    history.data.add(line.split(";"));
                }

            } while (true);

            if(history != null){
                data.add(history);
            }

            this.data = data;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

