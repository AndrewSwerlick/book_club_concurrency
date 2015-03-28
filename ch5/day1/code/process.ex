defmodule Dummy do
  def loop do
    receive do
      {:hi} ->
        IO.puts("hi")
        loop
    end
  end

  def stress(count) when count <=0 do 
    IO.puts("Done")
  end

  def stress(count) do
    spawn(__MODULE__, :loop,[])
    Dummy.stress(count - 1)
  end
end
