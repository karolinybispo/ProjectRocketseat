package com.rockset.plannner.activities;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityDate(UUID id, String title, LocalDateTime occurs_at) {
}
