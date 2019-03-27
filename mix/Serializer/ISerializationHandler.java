package Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by Niels Verheijen on 13/03/2019.
 */
public interface ISerializationHandler<REQUESTEDCLASS> {

    String serialize(REQUESTEDCLASS requestedclass) throws JsonProcessingException;
    REQUESTEDCLASS deserialize(String serializedString) throws IOException;

}
