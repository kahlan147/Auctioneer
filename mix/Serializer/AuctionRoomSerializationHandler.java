package Serializer;

import Classes.Auction;
import Classes.AuctionRoom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 15/03/2019.
 */
public class AuctionRoomSerializationHandler implements ISerializationHandler<AuctionRoom> {

    private ObjectMapper objectMapper;

    public AuctionRoomSerializationHandler(){
        objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize(AuctionRoom auctionRoom) throws JsonProcessingException {
        return objectMapper.writeValueAsString(auctionRoom);
    }

    @Override
    public AuctionRoom deserialize(String serializedString) throws IOException {
        return objectMapper.readValue(serializedString, AuctionRoom.class);
    }

}
