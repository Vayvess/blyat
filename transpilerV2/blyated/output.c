#include "runtime.h"

// === RECEPTOR: Creature ===
typedef struct {
    Int hp;
	Int hpMax;
	Int armor;
} Creature;

typedef struct {
    uint32_t len;
    uint32_t cap;
    uint32_t *pool;
    Creature *array;
} CreatureAllocator;

CreatureAllocator blyatCreatureAllocator;

void CreatureAllocator_init() {
    CreatureAllocator *alc = &blyatCreatureAllocator;
    int n = 8;
    alc->len = 0;
    alc->cap = n;
    alc->pool = malloc(sizeof(uint32_t) * n);
    for (int x = 0; x < n; x++) {
        alc->pool[x] = x;
    }
    alc->array = malloc(sizeof(Creature) * n);
}

uint32_t CreatureAllocator_alloc() {
    CreatureAllocator *alc = &blyatCreatureAllocator;
    if (alc->len >= alc->cap) {
        uint32_t z = alc->cap * 2;
        alc->array = realloc(alc->array, sizeof(Creature) * z);
        alc->pool = realloc(alc->pool, sizeof(uint32_t) * z);
        for (uint32_t x = alc->cap; x < z; x++) {
            alc->pool[x] = x;
        }
        alc->cap = z;
    }
    return alc->pool[alc->len++];
}

void Creature_free(uint32_t id) {
    CreatureAllocator *alc = &blyatCreatureAllocator;
    uint32_t x = --alc->len;
    alc->pool[x] = id;
}

void Creature_init(Int maxHp,Int armor) {
    uint32_t id = CreatureAllocator_alloc();
    Creature *self = blyatCreatureAllocator.array + id;
	self->hp = maxHp;
	self->hpMax = maxHp;
	self->armor = armor;

}


void Creature_damageTaken(Mail *mail) {
    Creature *self = blyatCreatureAllocator.array + mail->rid;
	Int amount = mail->args[0];

	amount -= self->armor;
	if (amount < 0) {
		amount = 0;
	}
	self->hp -= amount;

}

void Creature_sandbox(Mail *mail) {
    Creature *self = blyatCreatureAllocator.array + mail->rid;
	Int a = mail->args[0];
	Int b = mail->args[1];
	Int c = mail->args[2];

	int x = 5 * 8 + 3;
	if (a < b) {
		x *= 2;
	}
	else if (b < c) {
		x *= 3;
		x -= 2;
	}
	else {
		x -= 8;
		x = 7 * 3 + 2;
	}
	self->array->push(8, "je ne sais pas", false, self->other->another(8 + 5));
}
