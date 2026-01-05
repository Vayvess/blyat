// === RECEPTOR: @ALIAS@ ===
typedef struct {
    @FIELDS@
} @ALIAS@;

typedef struct {
    uint32_t len;
    uint32_t cap;
    uint32_t *pool;
    @ALIAS@ *array;
} @ALIAS@Allocator;

@ALLOCATOR_TYPE@ @ALLOCATOR_NAME@;

void @ALLOCATOR_TYPE@_init() {
    @ALLOCATOR_TYPE@ *alc = &@ALLOCATOR_NAME@;
    int n = 8;
    alc->len = 0;
    alc->cap = n;
    alc->pool = malloc(sizeof(uint32_t) * n);
    for (int x = 0; x < n; x++) {
        alc->pool[x] = x;
    }
    alc->array = malloc(sizeof(@ALIAS@) * n);
}

uint32_t @ALLOCATOR_TYPE@_alloc() {
    @ALLOCATOR_TYPE@ *alc = &@ALLOCATOR_NAME@;
    if (alc->len >= alc->cap) {
        uint32_t z = alc->cap * 2;
        alc->array = realloc(alc->array, sizeof(@ALIAS@) * z);
        alc->pool = realloc(alc->pool, sizeof(uint32_t) * z);
        for (uint32_t x = alc->cap; x < z; x++) {
            alc->pool[x] = x;
        }
        alc->cap = z;
    }
    return alc->pool[alc->len++];
}

void @ALIAS@_free(uint32_t id) {
    @ALLOCATOR_TYPE@ *alc = &@ALLOCATOR_NAME@;
    uint32_t x = --alc->len;
    alc->pool[x] = id;
}

@MOLDER@

@REACTIONS@
