package Serializer.JacksonDeserializers;

import Classes.Auction;
import Classes.AuctionRoom;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public class AuctionRoomDeserializer extends StdDeserializer<AuctionRoom> {

    public AuctionRoomDeserializer(){
        this(null);
    }

    public AuctionRoomDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public AuctionRoom deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name = (String) node.get("name").asText();
        return new AuctionRoom(name);
    }
}
