//Author: Brady Brown
//Date: 4/23/24
//Class: Algorithms & Data Structures
//Assignment: Counter Sort Algorithm - Parallel
/*Description: This program implements the counter sort algorithm that was taught in class. 
This algorithm takes an array, increments the value of another array at the index of that value
and assigns them back into the array in the order of the values placed in the second array.
This program also uses the threads class found in java, in order to implement parallel programming. */


import java.util.Random;

public class Sorter extends Thread{
  //these are used to transfer data from our main function into our threads
  //the array we are sorting
  private int[] data;
  //where the thread should start sorting from
  private int start;
  //where the thread should end sorting from
  private int end;

  //Simple constructor method where you can enter the data for the Sorter method
  Sorter(int[] data, int start, int end){
    this.data = data;
    this.start = start;
    this.end = end;
  }
  //main method that checks algorithm
  public static void main(String[] args){

  //This checks that there is only 1 integer value as a command line argument
    int loops = 0;
    if (args.length == 1) {
      try{
        loops = Integer.parseInt(args[0]);
      }catch (NumberFormatException e) {
        System.err.println("Argument" + args[0] + " must be an integer.");
        System.exit(1);
      }
    }else{
      System.out.println("Improper amount of arguments passed\nPlease enter one argument");
      System.exit(1);
    }
    //This creates a new random object for the arrays
    Random rand = new Random();
    //This runs the amount of loops specified in the command line argument
    for(int i = 0; i < loops; i++){

      //Prints the loop the function is currently on
      System.out.println("\n\nLoop " + (i+1) + ":");

      //Gives a random value from 1-20
      int size = rand.nextInt(20) + 1;

      //Dynamically allocates an array of integers of the size of the random value given
      int[] nums = new int[size];

      //This sets the value for each index in the array to a different random number from 1-20
      for(int j = 0; j < size; j++){
        nums[j] = rand.nextInt(20) + 1;
      }
      
      //This prints out the values of each index of the initial array
      System.out.println("\ninitial array:");
      for(int j = 0; j < size; j++){
        System.out.print(nums[j] + " ");
      }

      //This runs the sorting algorithm
      int[] solution = CounterSort(nums, size);

      //This prints out the supposedly sorted array
      System.out.println("\nsorted array: ");
      for(int j = 0; j < size; j++){
        System.out.print(solution[j] + " ");
      }

      //This checks to make sure that each value in the array is greater than the previous numbers in the array
      //Otherwise, it will have an error
      for(int j = 0; j < size - 1; j++){
        if (solution[j] > solution[j+1]){
          System.out.println("Something went wrong");
          System.exit(1);
        }
      }
    }
  }

  public static int[] CounterSort(int[] data, int length){
    
    //This variable controls the number of threads used
    int numThreads = 4;

    //This array of the sorter class is to make it easier to access all of...
    //our threads with less lines of code
    Sorter[] threads = new Sorter[numThreads];

    //this shows how much of the array each thread should take
    //calculated by using the number of threads and the length of the array
    int chunkSize = length / numThreads;

    //this finds the start and end point for each thread...
    //creates the thread and then starts it
    for (int j = 0; j < numThreads; j++) {
      int start = j * chunkSize;
      int end;
      //if the chunk size is bigger than the amount of integers left...
      //the end should at the end of the array
      if(j == numThreads - 1){
        end = length;
      }else{
        end = (j+1) * chunkSize;
      }        
      threads[j] = new Sorter(data, start, end);
      threads[j].start();
    }

    //This makes sure that each thread is finished before the main program continues
    try {
      for (int i = 0; i < threads.length; i++){
        threads[i].join();
      }
    //catches if a thread is interrupted for some reason
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    //This function is called in order to combine...
    //the separate arrays all into one array
    int [] merged = mergeArrays(data, threads);
    
    //This returns the now sorted array
    return merged;
  }

  //This method is what happens for each thread that starts
  public void run(){
    
    //integer to store biggest number in array
    int max = 0;

    //for loop to iterate through array and find the largest value
    for (int i = 0; i < data.length; i++) {
      //if the current value of the array is bigger than the max, update the max to the value
      if (data[i] > max) {
        max = data[i];
      }
    }
    //Increase the max by one, as it is used to create the size of the array in the next part
    //Otherwise, we will run into index out of bounds errors
    max++;

    //Dynamically allocates an array of integers of the size of the maximum value found
    int[] sorter = new int[max];

    //This for loop goes through the initial data array and increases...
    //the sorter value at the index of the data value at the index of the loop
    //This essentially counts the amount of each number that are in the array...
    //segment that is meant for the thread
    //The segment is determined through its start end end points
    for (int i = start; i < end; i++) {
      sorter[data[i]]++;
    }

    //There are essentially 3 counters here
    //The first for loop goes through the entire sorter array
    //The second for loop goes from 0 to the value of the sorter at i (first for loop)
    //This for loop is there to check what the value is at each index in the sorter array
    //If it is 0, it skips it
    //But if it is more than 0, it sets the data array at the index of the counter variable to i
    //Then the counter variable increases
    //If the sorter value at i is higher than 1, it will put another value of i into the array
    //Otherwise, it moves on to the next index in the sorter array
    int counter = start;
    for (int i = 0; i < max; i++) {
      for (int j = 0; j < sorter[i]; j++) {
          data[counter] = i;
          counter++;
      }
    }

  }

  public static int[] mergeArrays(int[] arr, Sorter[] threads) {
    //Arrays created to hold the now merged integers and...
    //The indexes of each array as it is iterated through
    int[] merged = new int[arr.length];
    int[] indexes = new int[threads.length];

    //Starts indices at the beginning of each segment
    for (int i = 0; i < threads.length; i++) {
      indexes[i] = threads[i].start;
    }

    //Merges sorted segments into one array
    //There are two for loops, and the first one is iterating through the data array
    //This is done so the data array can get move to the next spot once a value is placed
    for (int i = 0; i < arr.length; i++) {
      //Minimum values are created with values that are absurd for the program to reach
      int minIndex = -1;
      int minValue = 9999;
      //The second for loop is iterating through the threads array
      //This is done so that every thread array is checked for the next lowest value
      for (int j = 0; j < threads.length; j++) {
        //Integer that accesses the index that the thread array is currently at
        int index = indexes[j];
        //This if statement checks if the index is within the bounds of the...
        //thread's endpoint & checks if the value is less than the minimum value
        if (index < threads[j].end && threads[j].data[index] < minValue) {
          //If so, the minimum value is updated to the data of the thread's value...
          //and the minimum index is updated to the now lowest index
          minValue = threads[j].data[index];
          minIndex = j;
        }
      }
      //The lowest value found is now put into the merged array
      //The indexes array at the index of the lowest value found is increased
      merged[i] = minValue;
      indexes[minIndex]++;
    }

    //the now merged arrays are returned
    return merged;
  }

}

