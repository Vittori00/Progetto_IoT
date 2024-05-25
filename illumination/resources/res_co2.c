#include "contiki.h"
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "coap-engine.h"

// Function declarations
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);
void initialize_co2_level(void);

// Global variables
int co2_level = 0;
int too_high = 1500;
int too_low = 400;
bool switched = 0;
int counter = 0;

// Event resource definition
EVENT_RESOURCE(res_co2,
               "title=\"CO2\";rt=\"\";obs",
               res_get_handler,
               NULL,
               NULL,
               NULL,
               res_event_handler);

// Initialize the CO2 level
void initialize_co2_level(void)
{
    co2_level = (rand() % 1800) + 200;
}

// Event handler function
static void res_event_handler(void)
{
    if (co2_level == 0 || counter == 2 || switched)
    {
        initialize_co2_level();
        counter = 0;
        switched = false;
        coap_notify_observers(&res_co2);
    }else if (co2_level < too_low || co2_level > too_high) // Check if CO2 level is out of range
    {
        switched = true;
        // Notify all the observers
        coap_notify_observers(&res_co2);
    }
    counter++;
}

// GET handler function
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"co2_level\": %d}", co2_level);
    coap_set_payload(response, buffer, strlen((char *)buffer));
}
