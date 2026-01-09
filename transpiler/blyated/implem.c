#include "blyatypes.h"

#define TYPE Creature
#include "allocator_template.h"

void Creature_init(CreatureAllocator *alc, Int maxHp, Int armor) {
	uint32_t id = Creature_alloc(alc);
	Creature *self = &alc->array[id];
	self->hp = maxHp;
	self->hpMax = maxHp;
	self->armor = armor;
}

void Creature_damageTaken(Mail *mail) {
	Creature *self = &blyatCreatureAllocator->array[mail->target];
	Int amount = mail->payload.damageTaken.amount;
	Int multiplier = mail->payload.damageTaken.multiplier;
	amount -= self->armor;
	self->hp -= amount * multiplier;
}
int main() {
	return 0;
}
