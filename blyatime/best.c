#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <pthread.h>

typedef void (*Reaction)(uint32_t, uint32_t *);

typedef struct Stimulus {
    uint32_t rid;
    uint32_t arga;
    uint32_t argb;
    uint32_t argc;
    uint32_t argd;
    
    Reaction reaction;
    struct Stimulus *next;
} Stimulus;

typedef struct {
    Stimulus *head;
    Stimulus *tail;
    pthread_cond_t wait;
    pthread_mutex_t lock;
} Synapse;

void Synapse_init(Synapse *self) {
    self->head = NULL;
    self->tail = NULL;
    pthread_cond_init(&self->wait, NULL);
    pthread_mutex_init(&self->lock, NULL);
}

Stimulus *Synapse_get(Synapse *self) {
    pthread_mutex_lock(&self->lock);
    while (self->head == NULL) {
        pthread_cond_wait(&self->wait, &self->lock);
    }

    Stimulus *output = self->head;
    self->head = output->next;

    if (self->head == NULL) {
        self->tail = NULL;
    }

    pthread_mutex_unlock(&self->lock);
    return output;
}

void Synapse_put(Synapse *self, Stimulus *stimulus) {
    stimulus->next = NULL;
    pthread_mutex_lock(&self->lock);

    if (self->tail) {
        self->tail->next = stimulus;
    } else {
        self->head = stimulus;
    }
    self->tail = stimulus;
    
    pthread_cond_signal(&self->wait);
    pthread_mutex_unlock(&self->lock);
}

void *Effector(void *arg) {
    Synapse *syn = (Synapse *) arg;

    int runflag = 1;
    while (runflag) {
        Stimulus *stimulus = Synapse_get(syn);

        stimulus->reaction()
    }
}

int main(int argc, char const *argv[]) {

    return 0;
}
