package rgl.util;

import com.google.protobuf.GeneratedMessageV3;


public interface Serializable<T extends GeneratedMessageV3> {
    /**
     * Convert object to protobuf message
     */
    T serializeToProto();

    /**
     * Restore the object from protobuf message
     */
    void deserializeFromProto(T object);
}
