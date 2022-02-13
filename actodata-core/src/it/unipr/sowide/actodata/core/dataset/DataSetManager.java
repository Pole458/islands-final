package it.unipr.sowide.actodata.core.dataset;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.dataflow.DataTicket;
import it.unipr.sowide.actodata.core.dataset.content.crud.*;
import it.unipr.sowide.actodata.core.dataset.content.manycrud.*;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.annotations.Open;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.unipr.sowide.util.CheckedCast.cast;
import static it.unipr.sowide.util.Pair.pair;
import static it.unipr.sowide.util.Streams.stream;

/**
 * A special behavior used to manage a set of data. An actor executing this
 * behavior is supposed to act as an auxiliary actor, providing CRUD services on
 * the data it manages to other actors.
 *
 * @param <K> the type of the keys used to identify istances of the data
 * @param <T> the type of the instances
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class DataSetManager<K, T> extends ActoDataBaseBehavior {

    /**
     * {@inheritDoc}
     **/
    @Override
    public void actoDataNodeCases(ActoDataCaseFactory c) {
        c.onContentOfType(AddData.class, (addData, message) -> {
            cast((T) null, addData.getData()).ifSuccessfulOrElse((data) -> {
                send(message, new Added<>(this.add(data)));
            }, (/*else*/) -> {
                send(message, Error.UNKNOWNCONTENT);
            });
        });

        c.onContentOfType(AddManyData.class, ((addManyData, message) -> {
            cast((Iterable<T>) null, addManyData.getManyData())
                    .ifSuccessfulOrElse((data) -> {
                        send(message, new AddedMany<>(
                                stream(data)
                                        .map(this::add)
                                        .collect(Collectors.toList())
                        ));
                    }, (/*else*/) -> {
                        send(message, Error.UNKNOWNCONTENT);
                    });
        }));

        c.onContentOfType(GetData.class, (getData, message) -> {
            cast((K) null, getData.getKey()).ifSuccessfulOrElse((key) -> {
                get(key).ifPresentOrElse((data) -> {
                    send(message, new Found<>(key, data));
                }, (/*else*/) -> {
                    send(message, new NotFound<>(key));
                });
            }, (/*else*/) -> {
                send(message, Error.UNEXPECTEDCONTENT);
            });
        });

        c.onContentOfType(GetManyData.class, (getManyData, message) -> {
            cast((Iterable<K>) null, getManyData.getKeys())
                    .ifSuccessfulOrElse((keys) -> {
                        List<Pair<K, T>> results = stream(keys)
                                .map(key -> pair(key, get(key)))
                                .filter(pair -> pair.get2().isPresent())
                                .map(pair -> pair.map2(Optional::get))
                                .collect(Collectors.toList());
                        if (results.isEmpty()) {
                            send(message, new NotFoundMany<>(keys));
                        } else {
                            send(message, new FoundMany<>(results));
                        }
                    }, (/*else*/) -> {
                        send(message, Error.UNEXPECTEDCONTENT);
                    });
        });

        c.onContentOfType(GetDataTicket.class, (getDataTicket, message) -> {
            cast((K) null, getDataTicket.getKey()).ifSuccessfulOrElse((key) -> {
                get(key).ifPresentOrElse((data) -> {
                    send(message, new FoundDataTicket<>(key, new DataTicket<>(
                            data.getClass().getName(),
                            DataSetManager.this.getReference(),
                            new GetDataUnwrapped<>(key)
                    )));
                }, (/*else*/) -> {
                    send(message, new NotFound<>(key));
                });
            }, (/*else*/) -> {
                send(message, Error.UNEXPECTEDCONTENT);
            });
        });

        c.onContentOfType(GetManyDataTicket.class, (get, message) -> {
            cast((Iterable<K>) null, get.getKeys())
                    .ifSuccessfulOrElse((keys) -> {
                        stream(keys)
                                .map(this::get)
                                .filter(Optional::isPresent)
                                .findAny().ifPresentOrElse(dataSample -> {
                            send(message, new ManyDataTicket<>(
                                    keys,
                                    new DataTicket<>(
                                            dataSample.getClass().getName(),
                                            DataSetManager.this.getReference(),
                                            new GetManyDataUnwrapped<>(keys)
                                    )
                            ));
                        }, (/*else*/) -> {
                            send(message, new NotFoundMany<>(keys));
                        });
                    }, (/*else*/) -> {
                        send(message, Error.UNEXPECTEDCONTENT);
                    });
        });

        c.onContentOfType(GetDataUnwrapped.class, ((getUnwrapped, message) -> {
            cast((K) null, getUnwrapped.getKey())
                    .ifSuccessfulOrElse((key) -> {
                        get(key).ifPresentOrElse((data) -> {
                            send(message, data);
                        }, (/*else*/) -> {
                            send(message, Error.FAILEDEXECUTION);
                        });
                    }, (/*else*/) -> {
                        send(message, Error.UNEXPECTEDCONTENT);
                    });
        }));

        c.onContentOfType(GetManyDataUnwrapped.class, (manyUnw, message) -> {
            cast((Iterable<K>) null, manyUnw.getKeys())
                    .ifSuccessfulOrElse((keys) -> {
                        List<Pair<K, T>> results = stream(keys)
                                .map(key -> pair(key, get(key)))
                                .filter(pair -> pair.get2().isPresent())
                                .map(pair -> pair.map2(Optional::get))
                                .collect(Collectors.toList());
                        send(message, results);
                    }, (/*else*/) -> {
                        send(message, Error.UNEXPECTEDCONTENT);
                    });
        });

        c.onContentOfType(PutData.class, (putData, message) -> {
            cast((PutData<K, T>) null, putData).ifSuccessfulOrElse(ktput -> {
                put(ktput.getKey(), ktput.getData());
                send(message, Done.DONE);
            }, (/*else*/) -> {
                send(message, Error.UNEXPECTEDCONTENT);
            });
        });

        c.onContentOfType(PutManyData.class, (put, message) -> {
            cast((PutManyData<K, T>) null, put).ifSuccessfulOrElse(ktPut -> {
                for (Pair<K, T> dataPair : ktPut.getDataPairs()) {
                    dataPair.biConsume(this::put);
                }
                send(message, Done.DONE);
            }, (/*else*/) -> {
                send(message, Error.UNEXPECTEDCONTENT);
            });
        });

        c.serveProperty("size", this::size);


        datasetManagerCases(c);
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public final void onStart() {
        super.onStart();
        this.setup();
    }

    /**
     * Method executed before receiving requests on the data from other actors.
     */
    @Open
    public void setup() {
        // override if needed
    }

    /**
     * Method that can be executed to set custom cases (i.e. message
     * pattern/handler pairs) for this {@link DataSetManager}.
     *
     * @param c the case factory
     */
    @Open
    public void datasetManagerCases(ActoDataCaseFactory c) {
        // override if needed
    }

    /**
     * Implement this method to define the basic operation of reading data
     *
     * @param key the key of the requested data
     * @return an optional containing the found data if present,
     * {@link Optional#empty()} otherwise
     */
    protected abstract Optional<T> get(K key);

    /**
     * Implement this method to define the basic operation of adding data
     * to the set
     *
     * @param instance the instance of data to be added
     * @return the key assigned by the manager to the data, such that
     * {@link #get(Object) get(key)} returns the instance
     */
    protected abstract K add(T instance);

    /**
     * Implement this method to define the basic operation of adding/overwriting
     * a data instance to the slot assigned to the specified key.
     *
     * @param key      the key
     * @param instance the instance
     */
    protected abstract void put(K key, T instance);

    /**
     * Implement this method to define the basic operation of counting the data
     * instances managed by this dataset manager
     *
     * @return the size of the dataset
     */
    protected abstract long size();

}
