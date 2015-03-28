defmodule Priority do
  def start do
    spawn(__MODULE__, :loop, [:high])
  end

  def loop(priority) when priority == :high do
    receive do
      {:high, msg} -> process(:high, msg)
    after
      10000 -> loop(:low)
    end
  end

  def loop(priority) when priority == :low do
    receive do
      {:low, msg} -> process(:low, msg)
    after
      10 -> loop(:high)
    end
  end

  def process(priority, msg) do
    IO.puts("Processing #{priority} priority #{msg}")
    loop(:high)
  end
end
