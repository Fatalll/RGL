package protobuf;

import com.google.protobuf.GeneratedMessageV3;



public interface Serializable<T extends GeneratedMessageV3> {
    T serializeToProto();
    void deserializeFromProto(T object);
}
