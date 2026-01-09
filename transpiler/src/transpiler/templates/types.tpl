#pragma once
#include "blyatime.h"

// === RECEPTORS ===
@RECEPTORS@
// === ALLOCATORS ===
@ALLOCATORS@
// === PAYLOAD ===
@PAYLOADS@

typedef struct Mail {
    uint32_t target;
    Payload payload;
    Reaction reaction;
} Mail;
