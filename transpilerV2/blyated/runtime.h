#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

typedef int32_t Int;

typedef struct {
    uint32_t rid;
    uint32_t args[4];
} Mail;