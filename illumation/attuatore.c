#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"

/* Log configuration */
#include "coap-log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

#define SERVER_EP "coap://[fd00::203:3:3:3]:5683"

char *service_url_co2 = "/co2";
char *service_url_light = "/light";
char *service_url_phase = "/phase";

#define TOGGLE_INTERVAL 10
PROCESS(er_example_client, "Erbium Example Client");
AUTOSTART_PROCESSES(&er_example_client);

static struct etimer et_light, et_co2, et_phase;
int light = 0;
int co2 = 0;
int phase = 0;

void client_chunk_handler_co2(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);

    // Directly parse the JSON response to extract the CO2 level
    sscanf((const char *)chunk, "{\"co2_level\": %d}", &co2);
    printf("CO2 level: %d\n", co2);
    //funzione cambio luci
}

void client_chunk_handler_light(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);

    // Directly parse the JSON response to extract the light status
    sscanf((const char *)chunk, "{\"light\": %d}", &light);
    printf("Light status: %d\n", light);
    //funzione cambi luci
}

void client_chunk_handler_phase(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);
    
        // Directly parse the JSON response to extract the light status
        sscanf((const char *)chunk, "{\"phase\": %d}", &phase);
    printf("Phase: %d\n", phase);
    // funzione cambio luci
}

PROCESS_THREAD(er_example_client, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    // invece dei timer avremo le subscribe
    etimer_set(&et_co2, TOGGLE_INTERVAL * CLOCK_SECOND);
    etimer_set(&et_light, TOGGLE_INTERVAL * 2 * CLOCK_SECOND);
    etimer_set(&et_phase, TOGGLE_INTERVAL * 3 * CLOCK_SECOND);
    // ricevuti i valori qui facciamo partire la funzione delle luci

    while (1)
    {
        PROCESS_YIELD();

        // qui invece dei timer avremo gli handler per quando arriva la notifica
        if (etimer_expired(&et_co2))
        {

            printf("--Requesting CO2 level--\n");
            coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
            coap_set_header_uri_path(request, service_url_co2);
            COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler_co2);
            // chiamata funzione che setta luce
            if (etimer_expired(&et_light))
            {
                printf("--Requesting light status--\n");
                coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
                coap_set_header_uri_path(request, service_url_light);
                COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler_light);
                // chiamata funzione che setta luce
                etimer_reset(&et_co2);
                etimer_reset(&et_light);
            }
            if (etimer_expired(&et_phase))
            {
                printf("--Requesting phase--\n");
                coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
                coap_set_header_uri_path(request, service_url_phase);
                COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler_phase);
                etimer_reset(&et_phase);
                // chiamata funzione che setta la luce
            }
            printf("\n--Done--\n");
        }
    }
    PROCESS_END();
}
