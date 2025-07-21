package com.example.realtimemessaging.grpc;

option java_multiple_files = true;
option java_package = "com.example.realtimemessaging.grpc";
option java_outer_classname = "ChatServiceProto_CS1";

// The main service for real-time messaging
service ChatService {
  // Establishes a bi-directional stream for real-time communication.
  // The client must send a valid authentication token in the gRPC metadata.
  rpc ConnectStream(stream ClientToServerMessage) returns (stream ServerToClientMessage);
}

// Represents the delivery status of a message.
enum MessageStatus {
  STATUS_UNSPECIFIED = 0;
  SENT = 1;
  DELIVERED = 2;
  SEEN = 3;
  FAILED = 4;
}

// A wrapper message sent from the client to the server over the stream.
message ClientToServerMessage {
  oneof payload {
    SendMessageRequest send_message = 1;
    UpdateStatusRequest update_status = 2;
  }
}

// A wrapper message sent from the server to the client over the stream.
message ServerToClientMessage {
  oneof event {
    WelcomeEvent welcome_event = 1;
    NewMessageEvent new_message_event = 2;
    MessageStatusUpdateEvent message_status_update_event = 3;
    StreamErrorEvent stream_error_event = 4;
  }
}

// Payload for sending a new message.
message SendMessageRequest {
  string chat_id = 1;
  string client_message_id = 2;
  string content = 3;
}

// Payload for updating the status of a message.
message UpdateStatusRequest {
  string chat_id = 1;
  string message_id = 2;
  MessageStatus status = 3;
}

// Sent by the server upon successful connection and authentication.
message WelcomeEvent {
  string session_id = 1;
  google.protobuf.Timestamp server_timestamp = 2;
}

// Event sent to clients when a new message is posted in a chat.
message NewMessageEvent {
  string message_id = 1;
  string chat_id = 2;
  string sender_id = 3;
  string content = 4;
  google.protobuf.Timestamp created_at = 5;
  string client_message_id = 6;
}

// Event sent to clients when a message's status changes.
message MessageStatusUpdateEvent {
  string message_id = 1;
  string chat_id = 2;
  MessageStatus status = 3;
  google.protobuf.Timestamp updated_at = 4;
  string updated_by_user_id = 5;
}

// A non-fatal error sent over the stream.
message StreamErrorEvent {
  string error_code = 1;
  string error_message = 2;
  string original_client_message_id = 3;
}
```
src/main/resources/application.properties
```properties
# gRPC Server Configuration
grpc.server.port=9090
grpc.server.security.enabled=false
```
src/main/resources/log4j2.xml
```xml