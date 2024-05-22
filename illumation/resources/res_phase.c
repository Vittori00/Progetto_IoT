#include "contiki.h"
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "coap-engine.h"

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_phase,
                  "title=\"Phase\";rt=\"\";obs",
                  res_get_handler,
                  NULL,
                  NULL,
                  NULL);

static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    // Generates random value: 0 if first phase, 1 if second phase
    int phase = rand() % 2;

    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"phase\": %d}", phase);
    coap_set_payload(response, buffer, strlen((char *)buffer));

}

