package com.example.batchwithoutweb.chunktask.reader;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomItemStreamReader implements ItemStreamReader<String> {

    private final List<String> items;
    //실패할 시점의 데이터를 DB에 저장할거다.
    //index를 초기화하고, 재시작 할 것인지 안할것인지 여부를 속성으로 둔다.
    private int index = -1;
    private boolean restart = false;

    public CustomItemStreamReader(List<String> items) {
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String item = null;
        if(this.index < this.items.size()){
            item = this.items.get(index);
            index++;
        }

        if(this.index == 6 && !restart){
            throw new RuntimeException("Restart is required");
        }
        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        //이미 DB에 index라는 문자가 저장이 되어있다는 이야기
        if(executionContext.containsKey("index")){
            //실행한 index를 DB에서 불러와서
            index = executionContext.getInt("index");
            //다시 재시작할 수 있게 한다.
            this.restart = true;
        } else {
            //index값 기반으로 실패한 지점에서 실패 지점을 저장하고, 다시 재시작할때, 실패지점부터 시작하도록한다.
            index = 0;
            executionContext.put("index", index);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        //Chunk마다 데이터를 읽어서 읽기를 완료할 때마다, update메소드가 호출된다.
        //그래서 여기서는 현재 상태정보를 저장하는 것
        executionContext.put("index", index);
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("Connection closed....");
    }
}
