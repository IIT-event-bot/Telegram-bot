package com.project.event.models;

import java.util.Map;

public record RabbitMessage(RabbitMessageMethodType method, Map<String, Object> body) {
}