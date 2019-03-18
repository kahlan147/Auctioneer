package Serializer;

import Classes.AuctionRoom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels Verheijen on 18/03/2019.
 */
public class AuctionRoomListSerializationHandler implements ISerializationHandler<List<AuctionRoom>> {

    private ObjectMapper objectMapper;

    public AuctionRoomListSerializationHandler(){
        objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize(List<AuctionRoom> auctionRoomList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(auctionRoomList);
    }

    @Override
    public List<AuctionRoom> deserialize(String serializedString) throws IOException {
        return objectMapper.readValue(serializedString, objectMapper.getTypeFactory().constructCollectionType(List.class, AuctionRoom.class));
    }
}
