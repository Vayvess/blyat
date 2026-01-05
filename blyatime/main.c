#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdatomic.h>

// MUST BE POWER OF 2 FOR FAST MODULO
#define NARGS 4
#define RING_SIZE 4096
#define MASK (RING_SIZE - 1)

typedef void (*Behavior)(uint32_t, uint32_t *);

typedef struct {
    _Atomic(uint32_t) ready;
    uint32_t rid;
    uint32_t args[NARGS];
    Behavior behavior;
} Command;

typedef struct {
    Command *commands;
    _Atomic(uint64_t) head_idx;
    uint64_t tail_idx;
} Batcher;

void batch_command(Batcher *batcher, uint32_t rid, Behavior b, uint32_t *args) {
    // CLAIM
    size_t x = sizeof(Command);
    uint64_t idx = atomic_fetch_add(&batcher->head_idx, 1);
    Command *c = &batcher->commands[idx & MASK];

    // BUSY-WAIT IF FULL
    while (atomic_loads(&c->ready) != 0);

    // STORE
    c->rid = rid;
    c->behavior = b;
    for (int x = 0; x < NARGS; x++) c->args[x] = args[x];

    // COMMIT
    atomic_store(&c->ready, 1);
}

void *executor(void *arg) {
    Batcher *batcher = (Batcher *) arg;

    while (1) {
        Command *c = batcher->commands + (batcher->tail_idx & MASK);

        if (atomic_load(&c->ready) == 1) {
            c->behavior(c->rid, c->args);
        }

        atomic_store(&c->ready, 0);
        batcher->tail_idx++;
    }
}

int main(int argc, char const *argv[]) {
    return 0;
}
