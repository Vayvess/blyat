#pragma once
#include "blyatime.h"

// === RECEPTORS ===
typedef struct {
	Int hp;
	Int hpMax;
	Int armor;
} Creature;


// === ALLOCATORS ===
typedef struct {
	Creature *array;
	uint32_t len;
	uint32_t cap;
	uint32_t *free;
	uint32_t free_len;
	uint32_t free_cap;
} CreatureAllocator;

static CreatureAllocator blyatCreatureAllocator[NTHREADS];


// === PAYLOAD ===
typedef union {
	struct {
		Int amount;
		Int multiplier;
	} damageTaken;
} Payload;

typedef struct Mail {
    uint32_t target;
    Payload payload;
    Reaction reaction;
} Mail;
