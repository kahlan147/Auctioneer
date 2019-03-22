package Serializer.JacksonDeserializers;

import Classes.Auction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class AuctionDeserializer extends StdDeserializer<Auction> {

    public AuctionDeserializer(){
        this(null);
    }

    public AuctionDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public Auction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String name = (String) node.get("name").asText();
        double highestBid = (double) node.get("highestBid").numberValue();
        String nameHighestBidder = (String) node.get("nameHighestBidder").asText();
        String auctionRoomId = (String) node.get("auctionRoomId").asText();
        int auctionStartTime = Integer.parseInt(node.get("auctionStartTime").asText());
        int duration = Integer.parseInt(node.get("auctionDuration").asText());

        Auction auction = new Auction(name, highestBid, nameHighestBidder);
        auction.setAuctionRoomId(auctionRoomId);
        auction.setAuctionStartTime(auctionStartTime);
        auction.setAuctionDuration(duration);
        return auction;
    }
}
