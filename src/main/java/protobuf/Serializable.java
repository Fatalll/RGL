package protobuf;

import com.google.protobuf.GeneratedMessageV3;



public interface Serializable<T extends GeneratedMessageV3> {
    /**
     * @return Convert object to protobuf message
     */
    T serializeToProto();

    /**
     * @param object Restore the object from protobuf message
     */
    void deserializeFromProto(T object);
}
