typedef struct {
    int armor;
    int health;
    int maxHealth;
} Creature;

typedef struct {
    int totalXp;
} Player;

Creature CREATURES[24];

void damageTaken(int creature, int player, int amount) {
    Creature *self = CREATURES + creature;

    self->health -= amount;
}
