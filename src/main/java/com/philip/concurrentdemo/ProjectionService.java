package com.philip.concurrentdemo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ProjectionService {

    private final ThreadPoolExecutor projectionExecutor;

    public ProjectionService(ThreadPoolExecutor projectionExecutor){
        this.projectionExecutor = projectionExecutor;
    }

    public PickList buildProjections(PickList pickList, List<String> projectionList) {

        CompletableFuture<String> operationA = null;
        CompletableFuture<String> operationB = null;
        List<CompletableFuture> projectionFutures = new ArrayList<>();

        System.out.println("Building Futures to finish!");
        System.out.println("------------------------ Thread Pool Executor Info START ------------------------");
        System.out.println("Active Count: " + projectionExecutor.getActiveCount());
        System.out.println("Task Count: " + projectionExecutor.getTaskCount());
        System.out.println("Pool size: " + projectionExecutor.getPoolSize());
        System.out.println("Queue size: " + projectionExecutor.getQueue().size());
        System.out.println("------------------------ Thread Pool Executor Info END ------------------------");


        System.out.println("ThreadPoolExecutor: " + projectionExecutor.toString());

        for(String projection: projectionList)
        {
            switch(projection){
                case "projection1":
                    if(operationA == null){
                        operationA = CompletableFuture.supplyAsync(() -> gatherADetails(pickList), projectionExecutor);
                    }

                    CompletableFuture<Void> projection1 = operationA
                            .whenComplete((result, ex) -> {
                                if(null != ex){
                                    ex.printStackTrace();
                                }
                            })
                            .thenAccept(pickList::setADetails)
                            .thenApplyAsync((f) -> getProjectionOne(pickList), projectionExecutor)
                            .exceptionally((ex) -> {
                                    ex.printStackTrace();
                                    return "Exception while getting projection 1";
                            })
                            .thenAccept(pickList::setProjection1);

                    projectionFutures.add(projection1);

                    break;
                case "projection2":
                    if(operationA == null){
                        operationA = CompletableFuture.supplyAsync( () -> gatherADetails(pickList), projectionExecutor);
                    }

                    CompletableFuture<Void> projection2 = operationA
                            .whenComplete((result, ex) -> {
                                if(null != ex){
                                    ex.printStackTrace();
                                }
                            })
                            .thenApplyAsync((f) -> getProjectionTwo(pickList), projectionExecutor)
                            .exceptionally((ex) -> {
                                ex.printStackTrace();
                                return "Exception while getting projection 2";
                            })
                            .thenAccept(pickList::setProjection2);

                    projectionFutures.add(projection2);
                    break;
                case "projection3":
                    if(operationB == null){
                        operationB = CompletableFuture.supplyAsync( () -> gatherBDetails(pickList), projectionExecutor);
                    }

                    operationB.thenAccept(pickList::setBDetails);

                    CompletableFuture<Void> projection3 = operationB
                            .handle((result, ex) -> {
                                if(null != ex){
                                    ex.printStackTrace();
                                    return "Exception while fetching B details.";
                                }
                                return result;
                            })
                            .thenAccept(pickList::setBDetails)
                            .thenApplyAsync((f) -> getProjectionThree(pickList), projectionExecutor)
                            .exceptionally((ex) -> {
                                ex.printStackTrace();
                                return "Exception while getting projection 3";
                            })
                            .thenAccept(pickList::setProjection3);

                    projectionFutures.add(projection3);

                    break;
                case "projection4":
                    if(operationB == null){
                        operationB = CompletableFuture.supplyAsync( () -> gatherBDetails(pickList), projectionExecutor);
                    }

                    CompletableFuture<Void> projection4 = operationB
                            .handle((result, ex) -> {
                                if(null != ex){
                                    ex.printStackTrace();
                                    return "Exception while fetching B details.";
                                }
                                return result;
                            })
                            .thenAccept(pickList::setBDetails)
                            .thenApplyAsync(
                                    (f) -> getProjectionFour(pickList),
                                    projectionExecutor)
                            .exceptionally((ex) -> {
                                ex.printStackTrace();
                                return "Exception while getting projection 4";
                            })
                            .thenAccept(pickList::setProjection4);

                    projectionFutures.add(projection4);

                    break;
                case "projection5":
                    CompletableFuture<Void> projection5 = CompletableFuture.supplyAsync(
                            () -> getProjectionFive(pickList),
                            projectionExecutor)
                            .exceptionally((ex) -> {
                                ex.printStackTrace();
                                return "Exception while getting projection 5";
                            })
                            .thenAccept(pickList::setProjection5);

                    projectionFutures.add(projection5);
                    break;
                case "projection6":
                    CompletableFuture<Void> projection6 = CompletableFuture.supplyAsync(
                            () -> getProjectionSix(pickList),
                            projectionExecutor)
                            .exceptionally((ex) -> {
                                ex.printStackTrace();
                                return "Exception while getting projection 6";
                            })
                            .thenAccept(pickList::setProjection6);

                    projectionFutures.add(projection6);
                    break;
            }
        }
        System.out.println("Finished building futures!");


        System.out.println("Starting to wait for all futures to finish.");
        projectionFutures.forEach(future -> future.join());
        //CompletableFuture.allOf(projectionFutures.toArray(new CompletableFuture[projectionFutures.size()])).join();



        PickList output = new PickList();

        output.setProjectionsApplied(Arrays.toString(projectionList.toArray()));

        return output;
    }

    private String getProjectionOne(PickList pickList) {
        System.out.println("Started to getProjectionOne in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 2 == 0){
            System.out.println("Throwing exception in getProjectionOne::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        return "Projection-1-Result";
    }

    private String getProjectionTwo(PickList pickList) {
        System.out.println("Started to getProjectionTwo in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 9 == 0){
            System.out.println("Throwing exception in getProjectionTwo::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        return "Projection-2-Result";
    }

    private String getProjectionThree(PickList pickList) {
        System.out.println("Started to getProjectionThree in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 3 == 0){
            System.out.println("Throwing exception in getProjectionThree::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Exception while making request.");
        }

        return "Projection-3-Result";
    }

    private String getProjectionFour(PickList pickList) {
        System.out.println("Started to getProjectionFour in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 8 == 0){
            System.out.println("Throwing exception in getProjectionFour::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        return "Projection-4-Result";
    }

    private String getProjectionFive(PickList pickList) {
        System.out.println("Started to getProjectionFive in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 7 == 0){
            System.out.println("Throwing exception in getProjectionFive::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        return "Projection-5-Result";
    }

    private String getProjectionSix(PickList pickList) {
        System.out.println("Started to getProjectionSix in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 6 == 0){
            System.out.println("Throwing exception in getProjectionSix::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        return "Projection-6-Result";
    }

    private String gatherADetails(PickList pickList) {
        System.out.println("Started to gatherADetails in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 4 == 0){
            System.out.println("Throwing exception in gatherADetails::Thread: " + Thread.currentThread().getName());

            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        System.out.println("Returning from gatherADetails");
        return "A-Result";
    }

    private String gatherBDetails(PickList pickList) {
        System.out.println("Started to gatherBDetails in thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(getRandyWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(System.currentTimeMillis() % 5 == 0){
            System.out.println("Throwing exception in gatherBDetails::Thread: " + Thread.currentThread().getName());
            throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT, "Request timed out!");
        }


        System.out.println("Returning from gatherBDetails");
        return "B-Result";
    }

    private long getRandyWait() {
        Random r = new Random();
        int lower = 250;
        int higher = 2000;
        return r.nextInt((higher - lower + 1) + lower);
    }

}
