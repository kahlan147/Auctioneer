package Serializer;

import Classes.Auction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class AuctionSerializationHandler implements ISerializationHandler<Auction> {

    private ObjectMapper objectMapper;

    public AuctionSerializationHandler(){
        objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize(Auction auction) throws JsonProcessingException {
        return objectMapper.writeValueAsString(auction);
    }

    @Override
    public Auction deserialize(String serializedString) throws IOException {
        return objectMapper.readValue(serializedString, Auction.class);
    }
}
