#include "blyatime.h"

#ifndef TYPE
    #define TYPE Int
    #define IDE_SQUIGGLE_FIX
#endif

// HELPERS
#define _G(a, b) a##b
#define G(a, b) _G(a, b)

// IDENTIFIERS FOR INTERNAL LOGIC
#define ALLC_TYPE G(TYPE, Allocator)
#define ALLC_INIT G(ALLC_TYPE, _init)
#define ALLC_ALOC G(TYPE, _alloc)
#define ALLC_DLOC G(TYPE, _dlloc)

static inline void ALLC_INIT(ALLC_TYPE *alc) {
    int n = 8;
    alc->len = 0;
    alc->cap = n;
    alc->free_len = 0;
    alc->free_cap = n;

    alc->array = malloc(sizeof(TYPE) * n);
    alc->free = malloc(sizeof(uint32_t) * n);
}

static inline uint32_t ALLC_ALOC(ALLC_TYPE *alc) {
    // ALLOCATES PREVIOUS SLOT IF POSSIBLE
    if (alc->free_len) return alc->free[--alc->free_len];

    // GROW ARRAY IF NEEDED
    if (alc->len >= alc->cap) {
        uint32_t z = alc->cap << 1;
        alc->array = realloc(alc->array, sizeof(TYPE) * z);
        alc->cap = z;
    }

    return alc->len++;
}

static inline void ALLC_DLOC(ALLC_TYPE *alc, uint32_t id) {
    if (alc->free_len >= alc->free_cap) {
        uint32_t z = alc->free_cap << 1;
        alc->free = realloc(alc->free, sizeof(*alc->free) * z);
        alc->free_cap = z;
    }
    alc->free[alc->free_len++] = id;
}

#undef TYPE
#undef ALLC_TYPE
#undef ALLC_INIT
#undef ALLC_ALOC
#undef ALLC_DLOC
#undef G
#undef _G

#ifdef IDE_SQUIGGLE_FIX
    #undef TYPE
    #undef IDE_SQUIGGLE_FIX
#endif
