package com.example.demo.builder;

import com.example.demo.flatbuff.Monster;
import com.example.demo.flatbuff.Vec3;
import com.google.flatbuffers.FlatBufferBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

public class FlatBuffMessageBuilder {

    static Random random = new Random();

    public static Message<ByteBuffer> getMonsterMessage() {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int str = fbb.createString("MyMonster");
        Monster.startMonster(fbb);
        Monster.addPos(fbb, Vec3.createVec3(fbb, 1.0f, 2.0f, 3.0f));
        Monster.addHp(fbb, (short) 6);
        Monster.addName(fbb, str);
        int mon = Monster.endMonster(fbb);
        fbb.finish(mon);
        byte[] data = fbb.sizedByteArray();
        ByteBuffer bb = ByteBuffer.wrap(data);
        return MessageBuilder.withPayload(bb)
                .setHeader("tracer", UUID.randomUUID().toString()).build();
    }

}
