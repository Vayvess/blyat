#pragma once
#include <stdlib.h>
#include <stdint.h>
#include <pthread.h>

#define NTHREADS 4
#define MAILBOX_CAPACITY 4096

typedef uint32_t Int;

struct Mail;
typedef struct Mail Mail;
typedef void (*Reaction)(Mail *mail);

typedef struct {
    uint32_t cap;
    uint32_t head;
    uint32_t tail;
    uint32_t count;

    Mail* buffer;
    pthread_mutex_t mutex;
    pthread_cond_t  not_full;
    pthread_cond_t  not_empty;
} Mailbox;

typedef struct {
    uint32_t id;
    uint32_t running;
    Mailbox mailbox;
    pthread_t thread;
} ThreadContext;
